import java.io.*;
import java.util.*;
import java.util.List;
import java.nio.file.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Main {
    private static final String RESET = "\u001B[0m"; //reset warna
    private static Map<Character, String> blockColors = new HashMap<>();
    private static long count_bev1nd4 = 0;

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
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

    private static boolean solveRecursive(Board_Bev board, int blockIndex) {
        count_bev1nd4++;

        if (blockIndex == board.blocks.size()) {
            return isBoardFull(board);
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

    private static boolean isBoardFull(Board_Bev board) {
        for (int i = 0; i < board.baris; i++) {
            for (int j = 0; j < board.kolom; j++) {
                if (board.board[i][j] == '.') {
                    return false;
                }
            }
        }
        return true;
    }

    static boolean solvePuzzle(Board_Bev board) {
        int totalCells = board.baris * board.kolom;
        int totalBlockCells = 0;
        for (Board_Bev.Block block : board.blocks) {
            for (int i = 0; i < block.bentuk.length; i++) {
                for (int j = 0; j < block.bentuk[i].length; j++) {
                    if (block.bentuk[i][j] != false) {
                        totalBlockCells++;
                    }
                }
            }
        }
        
        if (totalBlockCells != totalCells) {
            System.out.println("\nTidak ada solusi: Ukuran total block (" + totalBlockCells + 
                             ") tidak sesuai dengan ukuran papan (" + totalCells + ")");
            return false;
        }
        
        return solveRecursive(board, 0);
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
        String txtPath = "test/" + baseName + ".txt";
        simpanSolusiTxt(board, txtPath, lamaExe);
        String imgPath = "test/" + baseName + ".png";
        simpanSolusiImage(board, imgPath, lamaExe);
        
        System.out.println("\nYay! Solusi telah berhasil disimpan ke: ");
        System.out.println("1. " + txtPath + " (text)");
        System.out.println("2. " + imgPath + " (image)");
    
    } catch (IOException e) {
        e.printStackTrace();
    }
}

    private static void simpanSolusiTxt(Board_Bev board, String filePath, long lamaExe) throws IOException {
        FileWriter writer = new FileWriter(filePath);
        writer.write("Kamu berhasil menemukan solusi puzzle :)\n\n");

        for (int i = 0; i < board.baris; i++) {
            for (int j = 0; j < board.kolom; j++) {
                writer.write(board.board[i][j]);
            }
            writer.write("\n");
        }

        writer.write("\nWaktu pencarian: " + lamaExe + " ms\n");
        writer.write("Banyak kasus yang ditinjau: " + count_bev1nd4);
        writer.close();
    }

    private static void simpanSolusiImage(Board_Bev board, String filePath, long lamaExe) throws IOException {
        final int CELL_SIZE = 45;
        final int PADDING = 50;
        final int CIRCLE_PADDING = 2;
        int width = board.kolom * CELL_SIZE + 2 * PADDING;
        int height = board.baris * CELL_SIZE + 2 * PADDING + 80; 
        
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        g2d.setColor(new Color(245, 247, 250));
        g2d.fillRect(0, 0, width, height);
        
        g2d.setColor(new Color(41, 128, 185));
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("Bev Blast :)", PADDING, 35);
        
        g2d.setColor(new Color(52, 73, 94, 50));
        g2d.fillRoundRect(PADDING - 8, PADDING + 20, 
                        board.kolom * CELL_SIZE + 26, 
                        board.baris * CELL_SIZE + 26, 15, 15);
        g2d.setColor(new Color(52, 73, 94));
        g2d.fillRoundRect(PADDING - 10, PADDING + 18, 
                        board.kolom * CELL_SIZE + 20, 
                        board.baris * CELL_SIZE + 20, 15, 15);
        
        Color[] blockColors = new Color[26];
        blockColors[0] = new Color(231, 76, 60);   
        blockColors[1] = new Color(41, 128, 185);  
        blockColors[2] = new Color(39, 174, 96);  
        blockColors[3] = new Color(241, 196, 15); 
        blockColors[4] = new Color(142, 68, 173);  
        blockColors[5] = new Color(230, 126, 34);  
        blockColors[6] = new Color(52, 152, 219);  
        blockColors[7] = new Color(46, 204, 113);  
        blockColors[8] = new Color(155, 89, 182);  
        blockColors[9] = new Color(26, 188, 156); 
        
        for (int i = 10; i < 26; i++) {
            float hue = (i * (360f / 16f)) / 360f;
            blockColors[i] = Color.getHSBColor(hue, 0.8f, 0.9f);
        }
        
        int circleSize = CELL_SIZE - CIRCLE_PADDING * 2;
        for (int i = 0; i < board.baris; i++) {
            for (int j = 0; j < board.kolom; j++) {
                int x = PADDING + j * CELL_SIZE + CIRCLE_PADDING;
                int y = PADDING + i * CELL_SIZE + CIRCLE_PADDING + 20;
                
                char c = board.board[i][j];
                if (c != '.') {
                    g2d.setColor(new Color(0, 0, 0, 30));
                    g2d.fillOval(x + 2, y + 2, circleSize, circleSize);
                    g2d.setColor(blockColors[c - 'A']);
                    g2d.fillOval(x, y, circleSize, circleSize);
                    
                    g2d.setColor(new Color(255, 255, 255, 60));
                    g2d.fillOval(x + 3, y + 3, circleSize/2, circleSize/2);
                    
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Arial", Font.BOLD, 16));
                    FontMetrics fm = g2d.getFontMetrics();
                    String str = String.valueOf(c);
                    int textX = x + (circleSize - fm.stringWidth(str)) / 2;
                    int textY = y + (circleSize + fm.getAscent() - fm.getDescent()) / 2;
                    g2d.drawString(str, textX, textY);
                } else {
                    g2d.setColor(new Color(60, 60, 60));
                    g2d.fillOval(x, y, circleSize, circleSize);
                }
            }
        }
        
        int statsY = height - PADDING/2;
        g2d.setColor(new Color(52, 73, 94));
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        g2d.drawString("Waktu pencarian: " + lamaExe + " ms", PADDING, statsY);
        g2d.drawString("Banyak kasus yang ditinjau: " + count_bev1nd4, PADDING, statsY + 20);
        
        g2d.dispose();
        ImageIO.write(image, "PNG", new File(filePath));
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