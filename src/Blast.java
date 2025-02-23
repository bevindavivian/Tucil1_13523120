import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.border.*;
import javax.swing.filechooser.FileFilter;

public class Blast extends JFrame {
    private Board_Bev board;
    private JPanel boardPanel;
    private JButton loadButton;
    private JButton solveButton;
    private JButton saveButton;
    private JTextArea statusSolver;
    private JLabel timeLabel;
    private JLabel casesLabel;
    private JLabel loadedFileLabel;
    private final Color[] BLOCK_COLORS;
    private static long count_bev1nd4 = 0;
    private final int CELL_SIZE = 45;
    private final int CIRCLE_PADDING = 2;

    public Blast() {
        super("Bev Blast (IQ Puzzler Pro Solver) :)");
        BLOCK_COLORS = generateColors();
        setupUI();
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        JPanel headerPanel = new GradientPanel();
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JLabel titleLabel = new JLabel("Bev Blast :)");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 0));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(245, 247, 250));

        JPanel boardContainer = new JPanel(new BorderLayout());
        boardContainer.setBorder(new ShadowBorder());
        boardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBoard(g);
            }
        };
        boardPanel.setPreferredSize(new Dimension(600, 400));
        boardPanel.setBackground(new Color(236, 240, 241));
        boardContainer.add(boardPanel);
        mainPanel.add(boardContainer, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setPreferredSize(new Dimension(300, 0));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        controlPanel.setBackground(new Color(245, 247, 250));

        JPanel fileInfoPanel = new JPanel();
        fileInfoPanel.setLayout(new BoxLayout(fileInfoPanel, BoxLayout.Y_AXIS));
        fileInfoPanel.setBackground(Color.WHITE);
        fileInfoPanel.setBorder(BorderFactory.createCompoundBorder(
            new ShadowBorder(),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        loadedFileLabel = new JLabel("File belum diload");
        loadedFileLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fileInfoPanel.add(loadedFileLabel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
            new ShadowBorder(),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        loadButton = createStyledButton("Load Puzzle", new Color(52, 152, 219));
        solveButton = createStyledButton("Solve Puzzle", new Color(46, 204, 113));
        saveButton = createStyledButton("Simpan Solusimu :)", new Color(155, 89, 182));

        solveButton.setEnabled(false);
        saveButton.setEnabled(false);

        buttonPanel.add(loadButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(solveButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        buttonPanel.add(saveButton);

        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setBackground(Color.WHITE);
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
            new ShadowBorder(),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        statusSolver = new JTextArea(4, 25);
        statusSolver.setEditable(false);
        statusSolver.setWrapStyleWord(true);
        statusSolver.setLineWrap(true);
        statusSolver.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusSolver.setBackground(Color.WHITE);
        statusSolver.setText("Upload file puzzlemu pada tombol Load Puzzle untuk mulai solve ya :)");

        JScrollPane scrollPane = new JScrollPane(statusSolver);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        statusPanel.add(scrollPane);

        timeLabel = new JLabel("Waktu pencarian: -");
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        casesLabel = new JLabel("Banyak kasus yang ditinjau: -");
        casesLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        statusPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        statusPanel.add(timeLabel);
        statusPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        statusPanel.add(casesLabel);

        controlPanel.add(fileInfoPanel);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        controlPanel.add(buttonPanel);
        controlPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        controlPanel.add(statusPanel);

        mainPanel.add(controlPanel, BorderLayout.EAST);
        add(mainPanel);

        setupButtonListeners();
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bgColor.brighter());
                } else {
                    g2.setColor(bgColor);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                
                super.paintComponent(g);
            }
        };
        
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(250, 40));
        button.setPreferredSize(new Dimension(250, 40));
        
        return button;
    }

    private void setupButtonListeners() {
        loadButton.addActionListener(e -> loadPuzzleWithValidation());
        solveButton.addActionListener(e -> solvePuzzle());
        saveButton.addActionListener(e -> saveSolution());
    }

    private void loadPuzzleWithValidation() {
        JFileChooser fileChooser = new JFileChooser("test");
        fileChooser.setFileFilter(new FileFilter() {
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".txt");
            }
            public String getDescription() {
                return "Text Files (*.txt)";
            }
        });

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                validateAndLoadPuzzle(file);
            } catch (Exception ex) {
                showError("Invalid Puzzle File", ex.getMessage());
            }
        }
    }

    private void validateAndLoadPuzzle(File file) throws Exception {
        List<String> allLines = Files.readAllLines(file.toPath());
        
        if (allLines.size() < 2) {
            throw new Exception("Input puzzle tidak valid :( \nFile terlalu pendek, silakan perbaiki file terlebih dahulu");
        }

        String[] dimensions = allLines.get(0).trim().split("\\s+");
        if (dimensions.length != 3) {
            throw new Exception("Input puzzle tidak valid :( \nFormat dimensi tidak sesuai, silakan perbaiki file terlebih dahulu");
        }

        try {
            int P = Integer.parseInt(dimensions[2]);

            if (P > 26) {
                throw new Exception("Input puzzle tidak valid :( \nJumlah blok puzzle tidak boleh lebih dari 26, silakan perbaiki file terlebih dahulu");
            }
            if (P <= 0) {
                throw new Exception("Input puzzle tidak valid :( \nJumlah blok puzzle harus positif, silakan perbaiki file terlebih dahulu");
            }
        } catch (NumberFormatException e) {
            throw new Exception("Input puzzle tidak valid :( \nFormat dimensi harus berupa angka, silakan perbaiki file terlebih dahulu");
        }

        String puzzleType = allLines.get(1).trim();
        if (!puzzleType.equals("DEFAULT")) {
            throw new Exception("Input puzzle tidak valid :( \nJenis puzzle harus DEFAULT, silakan perbaiki file terlebih dahulu");
        }

        loadPuzzleFromFile(file);
        loadedFileLabel.setText("Loaded: " + file.getName());
        solveButton.setEnabled(true);
        statusSolver.setText("Puzzle berhasil diload!\nBev Blast siap mencari solusinya :)");
        boardPanel.repaint();
    }

    private void showError(String title, String message) {
        JOptionPane.showMessageDialog(this,
            message,
            title,
            JOptionPane.ERROR_MESSAGE);
    }

    private void loadPuzzleFromFile(File file) throws IOException {
        Scanner scanner = new Scanner(file);
        String[] dimensions = scanner.nextLine().split(" ");
        int N = Integer.parseInt(dimensions[0]);
        int M = Integer.parseInt(dimensions[1]);
        int P = Integer.parseInt(dimensions[2]);
        
        board = new Board_Bev(N, M);
        scanner.nextLine(); 
        
        ArrayList<String> shapeLines = new ArrayList<>();
        char currentChar = '\0';
        Set<Character> uniqueBlocks = new HashSet<>();
        int blockCount = 0;
        
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
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
                addBlockToBoard(shapeLines, currentChar);
                shapeLines.clear();
                shapeLines.add(line);
                currentChar = firstNonSpace;
                uniqueBlocks.add(firstNonSpace);
            }
        }
        
        if (!shapeLines.isEmpty()) {
            blockCount++;
            addBlockToBoard(shapeLines, currentChar);
        }

        if (blockCount != P) {
            throw new IOException("Input puzzle tidak valid :( \nJumlah blok (" + blockCount + ") tidak sesuai dengan P (" + P + "), silakan perbaiki file terlebih dahulu");
        }
        if (uniqueBlocks.size() != P) {
            throw new IOException("Input puzzle tidak valid :( \nAda blok duplikat, setiap blok harus unik, silakan perbaiki file terlebih dahulu");
        }
        
        scanner.close();
    }

    private void addBlockToBoard(ArrayList<String> shapeLines, char currentChar) {
        int rows = shapeLines.size();
        int cols = shapeLines.stream().mapToInt(String::length).max().orElse(0);
        char[][] pieceMatrix = new char[rows][cols];
        
        for (int i = 0; i < rows; i++) {
            Arrays.fill(pieceMatrix[i], '.');
        }
        
        for (int i = 0; i < rows; i++) {
            String row = shapeLines.get(i);
            for (int j = 0; j < row.length(); j++) {
                char c = row.charAt(j);
                pieceMatrix[i][j] = (c != ' ') ? c : '.';
            }
        }
        
        board.tambahBlock(currentChar, pieceMatrix);
    }
    
    private boolean isBoardFull(Board_Bev board) {
        for (int i = 0; i < board.baris; i++) {
            for (int j = 0; j < board.kolom; j++) {
                if (board.board[i][j] == '.') {
                    return false;
                }
            }
        }
        return true;
    }

    private void validateTotalCells() {
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
            statusSolver.setText("Maaf, tidak ada solusi dari persoalan ini :(\nTotal block cells (" + totalBlockCells + 
                            ") tidak cocok dengan total cells (" + totalCells + ")");
            throw new IllegalStateException("Total block cells tidak cocok dengan ukuran papan");
        }
    }

    private void solvePuzzle() {
        count_bev1nd4 = 0;
        solveButton.setEnabled(false);
        statusSolver.setText("Solving puzzle...");
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            private long startTime;
            private long endTime;
            
            @Override
            protected Boolean doInBackground() {
                try {
                    validateTotalCells(); 
                    startTime = System.currentTimeMillis();
                    boolean solved = solveRecursive(board, 0);
                    endTime = System.currentTimeMillis();
                    return solved;
                } catch (IllegalStateException e) {
                    endTime = System.currentTimeMillis();
                    return false;
                }
            }
            
            @Override
            protected void done() {
                try {
                    boolean solved = get();
                    long duration = endTime - startTime;
                    
                    if (solved && isBoardFull(board)) {
                        statusSolver.setText("Kamu berhasil menemukan solusi puzzle :)");
                        timeLabel.setText("Waktu pencarian: " + duration + " ms");
                        casesLabel.setText("Banyak kasus yang ditinjau: " + count_bev1nd4);
                        saveButton.setEnabled(true);
                    } else {
                        String message = "Maaf, tidak ada solusi dari persoalan ini :(\n";
                        if (!isBoardFull(board)) {
                            message += "Papan tidak penuh setelah pencarian selesai.";
                        }
                        statusSolver.setText(message);
                        timeLabel.setText("Waktu pencarian: " + duration + " ms");
                        casesLabel.setText("Banyak kasus yang ditinjau: " + count_bev1nd4);
                        saveButton.setEnabled(false);
                    }
                    boardPanel.repaint();
                } catch (Exception ex) {
                    showError("Error", "Error solving puzzle: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private boolean solveRecursive(Board_Bev board, int blockIndex) {
        count_bev1nd4++;
        
        if (blockIndex == board.blocks.size()) {
            return isBoardFull(board); 
        }
        
        Board_Bev.Block currentBlock = board.blocks.get(blockIndex);
        
        for (int row = 0; row < board.baris; row++) {
            for (int col = 0; col < board.kolom; col++) {
                for (Board_Bev.Block rotatedBlock : getAllRotations(board, currentBlock)) {
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
    
    private ArrayList<Board_Bev.Block> getAllRotations(Board_Bev board, Board_Bev.Block block) {
        ArrayList<Board_Bev.Block> rotations = new ArrayList<>();
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

    private void saveSolution() {
        JFileChooser fileChooser = new JFileChooser("test");
        fileChooser.setSelectedFile(new File("solution"));
        fileChooser.setDialogTitle("Save Solution");
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String baseName = fileChooser.getSelectedFile().getAbsolutePath().replaceAll("\\.txt$|\\.png$", "");
            
            try {
                File txtFile = new File(baseName + ".txt");
                saveSolutionAsText(txtFile);
                File imgFile = new File(baseName + ".png");
                saveSolutionAsImage(imgFile);
                
                statusSolver.setText("Yay! Solusi telah berhasil disimpan ke:\nText file: " + txtFile.getName() + 
                                 "\nImage file: " + imgFile.getName());
            } catch (IOException ex) {
                showError("Error", "Error saving solution: " + ex.getMessage());
            }
        }
    }
    
    private void saveSolutionAsText(File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("Kamu berhasil menemukan solusi puzzle menggunakan Bev Blast :)\n\n");
            
            for (int i = 0; i < board.baris; i++) {
                for (int j = 0; j < board.kolom; j++) {
                    writer.write(board.board[i][j]);
                }
                writer.write("\n");
            }
            
            writer.write("\nWaktu pencarian: " + timeLabel.getText().substring(16));
            writer.write("\nBanyak kasus yang ditinjau: " + count_bev1nd4);
        }
    }
    
    private void saveSolutionAsImage(File file) throws IOException {
        int padding = 50;
        int titleHeight = 40;
        int width = board.kolom * CELL_SIZE + 2 * padding;
        int height = board.baris * CELL_SIZE + 2 * padding + titleHeight;
        
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        g2d.setColor(new Color(245, 247, 250));
        g2d.fillRect(0, 0, width, height);
        
        g2d.setColor(new Color(41, 128, 185));
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 20));
        g2d.drawString("Bev Blast :)", padding, padding/2);
        
        g2d.setColor(new Color(52, 73, 94, 50));
        g2d.fillRoundRect(padding - 8, padding + titleHeight - 8, 
                         board.kolom * CELL_SIZE + 26, 
                         board.baris * CELL_SIZE + 26, 15, 15);
        g2d.setColor(new Color(52, 73, 94));
        g2d.fillRoundRect(padding - 10, padding + titleHeight - 10, 
                         board.kolom * CELL_SIZE + 20, 
                         board.baris * CELL_SIZE + 20, 15, 15);
        
        int circleSize = CELL_SIZE - CIRCLE_PADDING * 2;
        for (int i = 0; i < board.baris; i++) {
            for (int j = 0; j < board.kolom; j++) {
                int x = padding + j * CELL_SIZE + CIRCLE_PADDING;
                int y = padding + titleHeight + i * CELL_SIZE + CIRCLE_PADDING;
                
                char c = board.board[i][j];
                if (c != '.') {
                    g2d.setColor(new Color(0, 0, 0, 30));
                    g2d.fillOval(x + 2, y + 2, circleSize, circleSize);
                    g2d.setColor(BLOCK_COLORS[c - 'A']);
                    g2d.fillOval(x, y, circleSize, circleSize);
                    
                    g2d.setColor(new Color(255, 255, 255, 60));
                    g2d.fillOval(x + 3, y + 3, circleSize/2, circleSize/2);
                    
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
                    FontMetrics fm = g2d.getFontMetrics();
                    String str = String.valueOf(c);
                    int textX = x + (circleSize - fm.stringWidth(str)) / 2;
                    int textY = y + (circleSize + fm.getAscent() - fm.getDescent()) / 2;
                    g2d.drawString(str, textX, textY);
                } else {
                    GradientPaint gradient = new GradientPaint(
                        x, y, new Color(60, 60, 60),
                        x + circleSize, y + circleSize, new Color(40, 40, 40)
                    );
                    g2d.setPaint(gradient);
                    g2d.fillOval(x, y, circleSize, circleSize);
                }
            }
        }
        
        g2d.setColor(new Color(52, 73, 94));
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        g2d.drawString("Solution Statistics:", padding, height - padding + 10);
        g2d.drawString(timeLabel.getText(), padding, height - padding + 30);
        g2d.drawString(casesLabel.getText(), padding, height - padding + 50);
        
        g2d.dispose();
        ImageIO.write(image, "PNG", file);
    }

    private void drawBoard(Graphics g) {
        if (board == null) return;
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int totalWidth = board.kolom * CELL_SIZE;
        int totalHeight = board.baris * CELL_SIZE;
        int startX = (boardPanel.getWidth() - totalWidth) / 2;
        int startY = (boardPanel.getHeight() - totalHeight) / 2;
        
        g2d.setColor(new Color(52, 73, 94, 50));
        g2d.fillRoundRect(startX - 8, startY - 8, totalWidth + 26, totalHeight + 26, 15, 15);
        g2d.setColor(new Color(52, 73, 94));
        g2d.fillRoundRect(startX - 10, startY - 10, totalWidth + 20, totalHeight + 20, 15, 15);
        
        int circleSize = CELL_SIZE - CIRCLE_PADDING * 2;
        for (int i = 0; i < board.baris; i++) {
            for (int j = 0; j < board.kolom; j++) {
                int x = startX + j * CELL_SIZE + CIRCLE_PADDING;
                int y = startY + i * CELL_SIZE + CIRCLE_PADDING;
                
                char c = board.board[i][j];
                if (c != '.') {
                    g2d.setColor(new Color(0, 0, 0, 30));
                    g2d.fillOval(x + 2, y + 2, circleSize, circleSize);
                    g2d.setColor(BLOCK_COLORS[c - 'A']);
                    g2d.fillOval(x, y, circleSize, circleSize);
                    
                    g2d.setColor(new Color(255, 255, 255, 60));
                    g2d.fillOval(x + 3, y + 3, circleSize/2, circleSize/2);
                    
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
                    FontMetrics fm = g2d.getFontMetrics();
                    String str = String.valueOf(c);
                    int textX = x + (circleSize - fm.stringWidth(str)) / 2;
                    int textY = y + (circleSize + fm.getAscent() - fm.getDescent()) / 2;
                    g2d.drawString(str, textX, textY);
                } else {
                    GradientPaint gradient = new GradientPaint(
                        x, y, new Color(60, 60, 60),
                        x + circleSize, y + circleSize, new Color(40, 40, 40)
                    );
                    g2d.setPaint(gradient);
                    g2d.fillOval(x, y, circleSize, circleSize);
                }
            }
        }
    }

    private Color[] generateColors() {
        Color[] colors = new Color[26];
        colors[0] = new Color(231, 76, 60);   
        colors[1] = new Color(41, 128, 185); 
        colors[2] = new Color(39, 174, 96);  
        colors[3] = new Color(241, 196, 15);  
        colors[4] = new Color(142, 68, 173);  
        colors[5] = new Color(230, 126, 34);  
        colors[6] = new Color(52, 152, 219);
        colors[7] = new Color(46, 204, 113);  
        colors[8] = new Color(155, 89, 182);  
        colors[9] = new Color(26, 188, 156); 
        
        for (int i = 10; i < 26; i++) {
            float hue = (i * (360f / 16f)) / 360f;
            colors[i] = Color.getHSBColor(hue, 0.8f, 0.9f);
        }
        return colors;
    }

    private class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            
            GradientPaint gradient = new GradientPaint(
                0, 0, new Color(41, 128, 185),
                getWidth(), 0, new Color(52, 152, 219)
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private class ShadowBorder extends AbstractBorder {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2.setColor(new Color(0, 0, 0, 20));
            g2.fillRoundRect(x + 2, y + 2, width - 4, height - 4, 10, 10);
            
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(x, y, width - 4, height - 4, 10, 10);
            
            g2.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(4, 4, 4, 4);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new Blast().setVisible(true);
        });
    }
}