import java.util.ArrayList;

public class Board_Bev {
    int baris, kolom;
    char[][] board;
    ArrayList<Block> blocks;

    public Board_Bev(int baris, int kolom) {
        this.baris = baris;
        this.kolom = kolom;
        this.board = new char[baris][kolom];
        this.blocks = new ArrayList<>();

        for (int i = 0; i < baris; i++) {
            for (int j = 0; j < kolom; j++) {
                board[i][j] = '.';
            }
        }
    }

    public void tambahBlock(char huruf, char[][] shape) {
        boolean[][] blockShape = convertArray(shape);
        blocks.add(new Block(huruf, blockShape));
    }

    private boolean[][] convertArray(char[][] shape) {
        int panjang = shape.length;
        int lebar = shape[0].length;
        boolean[][] booleanShape = new boolean[panjang][lebar];
        
        for (int i = 0; i < panjang; i++) {
            for (int j = 0; j < lebar; j++) {
                booleanShape[i][j] = (shape[i][j] != '.'); 
            }
        }
        return booleanShape;
    }

    boolean cekAvail(Block block, int baris, int kolom) {
        if (baris + block.height > this.baris || kolom + block.lebar > this.kolom) {
            return false;
        }

        for (int i = 0; i < block.height; i++) {
            for (int j = 0; j < block.lebar; j++) {
                if (block.bentuk[i][j] && board[baris + i][kolom + j] != '.') {
                    return false;
                }
            }
        }
        return true;
    }

    void taruhBlock(Block block, int baris, int kolom) {
        for (int i = 0; i < block.height; i++) {
            for (int j = 0; j < block.lebar; j++) {
                if (block.bentuk[i][j]) {
                    board[baris + i][kolom + j] = block.huruf;
                }
            }
        }
    }

    void hapusBlock(Block block, int baris, int kolom) {
        for (int i = 0; i < block.height; i++) {
            for (int j = 0; j < block.lebar; j++) {
                if (block.bentuk[i][j]) {
                    board[baris + i][kolom + j] = '.';
                }
            }
        }
    }

    public class Block {
        char huruf;
        boolean[][] bentuk;
        int height, lebar;

        public Block(char huruf, boolean[][] shape) {
            this.huruf = huruf;
            this.height = shape.length;
            this.lebar = shape[0].length;
            this.bentuk = shape;
        }

        public Block rotate() {
            boolean[][] rotatedShape = new boolean[lebar][height];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < lebar; j++) {
                    rotatedShape[j][height - 1 - i] = bentuk[i][j];
                }
            }
            return new Block(huruf, rotatedShape);
        }

        public Block flip() {
            boolean[][] flippedShape = new boolean[height][lebar];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < lebar; j++) {
                    flippedShape[i][lebar - 1 - j] = bentuk[i][j];
                }
            }
            return new Block(huruf, flippedShape);
        }
    }
}
