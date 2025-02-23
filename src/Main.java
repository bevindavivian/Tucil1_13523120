import java.io.*;
import java.util.*;
import java.nio.file.*;

public class Main {
    private static final String RESET = "\u001B[0m"; // reset warna
    private static Map<Character, String> blockColors = new HashMap<>();
    private static long count_bev1nd4 = 0;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("Masukkan nama file .txt yang ingin diuji: ");
            String fileName = scanner.nextLine();
            if (!fileName.endsWith(".txt")) {
                fileName += ".txt";
            }

            String filePath = "test/" + fileName;
            File file = new File(filePath);

            if (!file.exists()) {
                System.out.println("Error: File '" + filePath + "' tidak ditemukan!");
                return;
            }

            List<String> allLines = Files.readAllLines(file.toPath());
            
            if (allLines.size() < 2) {
                System.out.println("Input puzzle tidak valid :( \nFile terlalu pendek, silakan perbaiki file terlebih dahulu");
                return;
            }

            String[] dimensions = allLines.get(0).trim().split("\\s+");
            if (dimensions.length != 3) {
                System.out.println("Input puzzle tidak valid :( \nFormat dimensi tidak sesuai, silakan perbaiki file terlebih dahulu");
                return;
            }

            int N, M, P;
            try {
                N = Integer.parseInt(dimensions[0]);
                M = Integer.parseInt(dimensions[1]);
                P = Integer.parseInt(dimensions[2]);

                if (P > 26) {
                    System.out.println("Input puzzle tidak valid :( \nJumlah blok puzzle tidak boleh lebih dari 26, silakan perbaiki file terlebih dahulu");
                    return;
                }

                if (P <= 0) {
                    System.out.println("Input puzzle tidak valid :( \nJumlah blok puzzle harus positif, silakan perbaiki file terlebih dahulu");
                    return;
                }

            } catch (NumberFormatException e) {
                System.out.println("Input puzzle tidak valid :( \nFormat dimensi harus berupa angka, silakan perbaiki file terlebih dahulu");
                return;
            }

            String puzzleType = allLines.get(1).trim();
            if (!puzzleType.equals("DEFAULT")) {
                System.out.println("Input puzzle tidak valid :( \nProgam ini hanya dapat mencari solusi untuk puzzle DEFAULT, silakan perbaiki file terlebih dahulu");
                return;
            }

            System.out.println("Berhasil membaca file: " + filePath);
            Scanner fileScanner = new Scanner(file);
            fileScanner.nextLine(); 
            fileScanner.nextLine(); 

            System.out.println("\nBerikut ini informasi persoalan dari file yang diinput :)");
            System.out.println("Dimensi board: " + N + "x" + M);
            System.out.println("Jumlah blocks: " + P);
            System.out.println("Jenis puzzle: " + puzzleType);

            Board_Bev papan = new Board_Bev(N, M);

            warnaBlocks(); //memberi warna pada bloks

            List<String> shapeLines = new ArrayList<>();
            char currentChar = '\0';
            Set<Character> uniqueBlocks = new HashSet<>();
            int blockCount = 0;

            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (line.isEmpty()) continue;
                char firstNonSpace = ' ';
                for (char c : line.toCharArray()) {
                    if (c != ' ') {
                        firstNonSpace = c;
                        break;
                    }
                }
                if (firstNonSpace == ' ') continue;
                if (currentChar == '\0' || firstNonSpace == currentChar) {
                    shapeLines.add(line);
                    if (currentChar == '\0') {
                        currentChar = firstNonSpace;
                        uniqueBlocks.add(firstNonSpace);
                    }
                } else {
                    blockCount++;
                    simpanBlock(papan, shapeLines, currentChar);
                    shapeLines.clear();
                    shapeLines.add(line);
                    currentChar = firstNonSpace;
                    uniqueBlocks.add(firstNonSpace);
                }
            }
            if (!shapeLines.isEmpty()) {
                blockCount++;
                simpanBlock(papan, shapeLines, currentChar);
            }

            if (blockCount != P) {
                System.out.println("Input puzzle tidak valid :( \nJumlah blok (" + blockCount + ") tidak sesuai dengan P (" + P + "), silakan perbaiki file terlebih dahulu");
                return;
            }
            if (uniqueBlocks.size() != P) {
                System.out.println("Input puzzle tidak valid :(\nAda blok yang duplikat, setiap blok harus unik, silakan perbaiki file terlebih dahulu");
                return;
            }

            long awal = System.currentTimeMillis();
            boolean solved = solvePuzzle(papan);

            long akhir = System.currentTimeMillis();
            long lamaExe = akhir - awal;

            if (solved) {
                System.out.println("\nKamu berhasil menemukan solusi puzzle :)");
                printColoredBoard(papan);
                System.out.println("\nWaktu pencarian: " + lamaExe + " ms");
                System.out.println("Banyak kasus yang ditinjau: " + count_bev1nd4);
            } else {
                System.out.println("\nMaaf, tidak ada solusi dari persoalan ini :(");
                System.out.println("\nWaktu pencarian: " + lamaExe + " ms");
                System.out.println("Banyak kasus yang ditinjau: " + count_bev1nd4);
            }

            System.out.println("\nApakah kamu ingin menyimpan solusi? (ya/tidak)");
            String saveResponse = scanner.nextLine().toLowerCase();

            if (saveResponse.startsWith("y")) {
                System.out.println("\nMasukkan nama file output untuk disimpan (tanpa .txt): ");
                String pathFile = scanner.nextLine();

                simpanSolusi(papan, pathFile, lamaExe);
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    static void simpanBlock(Board_Bev board, List<String> shapeLines, char currentChar) {
        int baris = shapeLines.size();
        int kolom = shapeLines.stream().mapToInt(String::length).max().orElse(0);
        char[][] pieceMatrix = new char[baris][kolom];

        for (int i = 0; i < baris; i++) {
            Arrays.fill(pieceMatrix[i], '.');
        }

        for (int i = 0; i < baris; i++) {
            String row = shapeLines.get(i);
            for (int j = 0; j < row.length(); j++) {
                char c = row.charAt(j);
                if (c != ' ') {
                    pieceMatrix[i][j] = c;
                } else {
                    pieceMatrix[i][j] = '.';
                }
            }
        }

        board.tambahBlock(currentChar, pieceMatrix);
    }

    static boolean solvePuzzle(Board_Bev board) {
        return solveRecursive(board, 0);
    }

    private static boolean solveRecursive(Board_Bev board, int blockIndex) {
        count_bev1nd4++;

        if (blockIndex == board.blocks.size()) {
            return true;  
        }

        Board_Bev.Block currentBlock = board.blocks.get(blockIndex);

        for (int row = 0; row < board.baris; row++) {
            for (int col = 0; col < board.kolom; col++) {
                for (Board_Bev.Block rotatedBlock : getAllRotations(currentBlock)) {
                    if (board.cekAvail(rotatedBlock, row, col)) {
                        board.taruhBlock(rotatedBlock, row, col);

                        if (solveRecursive(board, blockIndex + 1)) {
                            return true;
                        }

                        board.hapusBlock(rotatedBlock, row, col);
                    }
                }
            }
        }
        return false;
    }

    private static List<Board_Bev.Block> getAllRotations(Board_Bev.Block block) {
        List<Board_Bev.Block> rotations = new ArrayList<>();
        rotations.add(block);
        rotations.add(block.rotate());
        rotations.add(block.rotate().rotate());
        rotations.add(block.rotate().rotate().rotate());
        rotations.add(block.flip());
        rotations.add(block.flip().rotate());
        rotations.add(block.flip().rotate().rotate());
        rotations.add(block.flip().rotate().rotate().rotate());
        return rotations;
    }

    private static void printColoredBoard(Board_Bev board) {
        for (int i = 0; i < board.baris; i++) {
            for (int j = 0; j < board.kolom; j++) {
                char c = board.board[i][j];
                if (c != '.') {
                    System.out.print(blockColors.get(c) + c + RESET);
                } else {
                    System.out.print(c);
                }
            }
            System.out.println();
        }
    }

    private static void simpanSolusi(Board_Bev board, String baseName, long lamaExe) {
        try {
            String filePath = "test/" + baseName + ".txt";
    
            FileWriter writer = new FileWriter(filePath);
            writer.write("Kamu berhasil menemukan solusi puzzle :)\n");
    
            for (int i = 0; i < board.baris; i++) {
                for (int j = 0; j < board.kolom; j++) {
                    writer.write(board.board[i][j]);
                }
                writer.write("\n");
            }
    
            writer.write("\nWaktu pencarian: " + lamaExe + " ms\n");
            writer.write("Banyak kasus yang ditinjau: " + count_bev1nd4);
            writer.close();
    
            System.out.println("Yay! Solusi telah berhasil disimpan ke: " + filePath);
    
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void warnaBlocks() {
        int colorIndex = 0;
        for (char c = 'A'; c <= 'Z'; c++) {
            int colorCode = 16 + (colorIndex * 4) % 256; 
            blockColors.put(c, "\u001B[38;5;" + colorCode + "m"); 
            colorIndex++;
        }
    }
}