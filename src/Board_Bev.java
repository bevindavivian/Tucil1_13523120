public class Board_Bev {
    long count_bev14;
    int baris, kolom;
    char[][] board;

    public Board_Bev(int baris, int kolom) {
        this.baris = baris;
        this.kolom = kolom;
        this.board = new char[baris][kolom];
        this.count_bev14 = 0;
        
        for (int i = 0; i < baris; i++) {
            for (int j = 0; j < kolom; j++) {
                board[i][j] = '.'; //menginisialisasi board kosong
            }
        }
    }

    boolean cekAvail(Block_Bev block, int baris, int kolom) {
        if (baris + block.height > baris || kolom + block.lebar > kolom) return false;
        
        for (int i = 0; i < block.height; i++) {
            for (int j = 0; j < block.lebar; j++) {
                if (block.bentuk[i][j] && board[baris + i][kolom + j] != '.') {
                    return false; //jika ada block yang bertabrakan maka false (cek bisa atau tidak block ditempatkan di posisi)
                }
            }
        }
        return true;
    }

    void taruhBlock(Block_Bev block, int baris, int kolom) {
        for (int i = 0; i < block.height; i++) {
            for (int j = 0; j < block.lebar; j++) {
                if (block.bentuk[i][j]) {
                    board[baris + i][kolom + j] = block.huruf; //menaruh block di posisi baris, kolom
                }
            }
        }
    }

    void hapusBlock(Block_Bev block, int baris, int kolom) {
        for (int i = 0; i < block.height; i++) {
            for (int j = 0; j < block.lebar; j++) {
                if (block.bentuk[i][j]) {
                    board[baris + i][kolom + j] = '.'; //hapus block di posisi baris, kolom
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
}