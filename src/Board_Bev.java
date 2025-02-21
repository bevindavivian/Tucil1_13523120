import java.util.ArrayList;

public class Board_Bev {
    long count_bev14;
    int baris, kolom;
    char[][] board;
    ArrayList<Block> blocks;

    public Board_Bev(int baris, int kolom) {
        this.baris = baris;
        this.kolom = kolom;
        this.board = new char[baris][kolom];
        this.count_bev14 = 0;
        this.blocks = new ArrayList<>();

        for (int i = 0; i < baris; i++) {
            for (int j = 0; j < kolom; j++) {
                board[i][j] = '.'; 
            }
        }
    }

    public void addBlock(char huruf, String[] shapeStr) {
        blocks.add(new Block(huruf, shapeStr));
    }

    boolean cekAvail(Block block, int baris, int kolom) {
        if (baris + block.height > this.baris || kolom + block.lebar > this.kolom) return false;

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

    void printBoard() {
        for (int i = 0; i < baris; i++) {
            for (int j = 0; j < kolom; j++) {
                System.out.print(board[i][j]);
            }
            System.out.println();
        }
    }

    public char[][] getBoard() {
        return board;
    }

    public class Block {
        char huruf;
        boolean[][] bentuk;
        int height, lebar;

        public Block(char huruf, String[] shapeStr) {
            this.huruf = huruf;
            this.height = shapeStr.length;
            this.lebar = 0;
            for (String baris : shapeStr) {
                this.lebar = Math.max(this.lebar, baris.length());
            }

            this.bentuk = new boolean[height][lebar];

            for (int i = 0; i < height; i++) {
                String baris = shapeStr[i];
                for (int j = 0; j < lebar; j++) {
                    if (j < baris.length()) {
                        this.bentuk[i][j] = (baris.charAt(j) == huruf);
                    } else {
                        this.bentuk[i][j] = false;
                    }
                }
            }
        }

        public Block rotate() {
            String[] rotated = new String[lebar];
            StringBuilder[] sb = new StringBuilder[lebar];

            for (int i = 0; i < lebar; i++) {
                sb[i] = new StringBuilder();
                for (int j = height - 1; j >= 0; j--) {
                    sb[i].append(bentuk[j][i] ? huruf : '.');
                }
                rotated[i] = sb[i].toString();
            }

            return new Block(huruf, rotated);
        }

        public Block flip() {
            String[] flipped = new String[height];
            for (int i = 0; i < height; i++) {
                StringBuilder sb = new StringBuilder();
                for (int j = lebar - 1; j >= 0; j--) {
                    sb.append(bentuk[i][j] ? huruf : '.');
                }
                flipped[i] = sb.toString();
            }
            return new Block(huruf, flipped);
        }
    }
}
