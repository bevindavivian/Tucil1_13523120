import java.io.*;
import java.util.*;

public class Main {
    private static final String[] COLORS = {
        "\u001B[31m", "\u001B[32m", "\u001B[33m", "\u001B[34m", "\u001B[35m", "\u001B[36m",
        "\u001B[91m", "\u001B[92m", "\u001B[93m", "\u001B[94m", "\u001B[95m", "\u001B[96m"
    };

    private static final String RESET = "\u001B[0m";
    private static Map<Character, String> blockColors = new HashMap<>();

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Masukkan nama file .txt yang ingin diuji/test (tanpa perlu menulis folder test/ atau ekstensi): ");
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

            System.out.println("Membaca file: " + filePath);
            Scanner fileScanner = new Scanner(file);
            String[] dimensions = fileScanner.nextLine().split(" ");
            int N = Integer.parseInt(dimensions[0]);
            int M = Integer.parseInt(dimensions[1]);
            int P = Integer.parseInt(dimensions[2]);

            System.out.println("Dimensi papan: " + N + "x" + M);
            System.out.println("Jumlah blok: " + P);

            String puzzleType = fileScanner.nextLine();
            System.out.println("Jenis puzzle: " + puzzleType);

            Board_Bev board = new Board_Bev(N, M);

            for (char c = 'A'; c <= 'Z'; c++) {
                blockColors.put(c, COLORS[(c - 'A') % COLORS.length]);
            }

            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (line.isEmpty()) continue;

                ArrayList<String> shapeLines = new ArrayList<>();
                char blockChar = line.charAt(0);

                System.out.println("Membaca blok " + blockChar + ":"); //debug

                do {
                    shapeLines.add(line);
                    if (!fileScanner.hasNextLine()) break;
                    line = fileScanner.nextLine().trim();
                } while (!line.isEmpty() && line.charAt(0) == blockChar);

                board.addBlock(blockChar, shapeLines.toArray(new String[0]));
            }

            fileScanner.close();

            boolean solved = solvePuzzle(board);

            if (solved) {
                System.out.println("\nSolusi ditemukan:");
                printColoredBoard(board);
            } else {
                System.out.println("\nTidak ada solusi!");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean solvePuzzle(Board_Bev board) {
        return solveRecursive(board, 0);
    }

    private static boolean solveRecursive(Board_Bev board, int blockIndex) {
        if (blockIndex == board.blocks.size()) {
            return true;
        }

        Board_Bev.Block block = board.blocks.get(blockIndex);

        for (int row = 0; row < board.baris; row++) {
            for (int col = 0; col < board.kolom; col++) {
                for (Board_Bev.Block rotatedBlock : getAllRotations(block)) {
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
        rotations.add(rotations.get(1).rotate());
        rotations.add(rotations.get(2).rotate());
        rotations.add(block.flip());
        rotations.add(rotations.get(4).rotate());
        rotations.add(rotations.get(5).rotate());
        rotations.add(rotations.get(6).rotate());
        return rotations;
    }

    private static void printColoredBoard(Board_Bev board) {
        for (int i = 0; i < board.baris; i++) {
            for (int j = 0; j < board.kolom; j++) {
                char c = board.board[i][j];
                if (c != '.') {
                    System.out.print(blockColors.get(c) + c + RESET); // Print color per block
                } else {
                    System.out.print(c);
                }
            }
            System.out.println();
        }
    }
}
