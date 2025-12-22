import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class EncryptionApp extends JFrame {
    private JTextArea inputTextArea;
    private JTextArea outputTextArea;
    private JComboBox<String> methodComboBox;
    private JTextField keyTextField;
    private JButton encryptButton;
    private JButton decryptButton;
    private JButton loadFileButton;
    private JButton saveFileButton;

    public EncryptionApp() {
        setTitle("Приложение для шифровки/дешифровки текста");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        initComponents();
        layoutComponents();
    }

    private void initComponents() {
        inputTextArea = new JTextArea(10, 30);
        outputTextArea = new JTextArea(10, 30);
        inputTextArea.setLineWrap(true);
        outputTextArea.setLineWrap(true);
        outputTextArea.setEditable(false);

        String[] methods = {"Шифр Цезаря", "Табличный шифр", "Шифр решеткой"};
        methodComboBox = new JComboBox<>(methods);

        keyTextField = new JTextField(20);

        encryptButton = new JButton("Зашифровать");
        decryptButton = new JButton("Расшифровать");
        loadFileButton = new JButton("Загрузить из файла");
        saveFileButton = new JButton("Сохранить в файл");

        // Обработчики событий
        encryptButton.addActionListener(e -> encryptText());
        decryptButton.addActionListener(e -> decryptText());
        loadFileButton.addActionListener(e -> loadFromFile());
        saveFileButton.addActionListener(e -> saveToFile());
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));

        // Панель ввода
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBorder(new TitledBorder("Исходный текст"));
        inputPanel.add(new JScrollPane(inputTextArea), BorderLayout.CENTER);

        // Панель вывода
        JPanel outputPanel = new JPanel(new BorderLayout(5, 5));
        outputPanel.setBorder(new TitledBorder("Результат"));
        outputPanel.add(new JScrollPane(outputTextArea), BorderLayout.CENTER);

        // Панель управления
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBorder(new TitledBorder("Параметры шифрования"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(new JLabel("Метод шифрования:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        controlPanel.add(methodComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        controlPanel.add(new JLabel("Ключ:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        controlPanel.add(keyTextField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);
        buttonPanel.add(loadFileButton);
        buttonPanel.add(saveFileButton);
        controlPanel.add(buttonPanel, gbc);

        // Основная компоновка
        JPanel textPanels = new JPanel(new GridLayout(1, 2, 10, 10));
        textPanels.add(inputPanel);
        textPanels.add(outputPanel);

        add(textPanels, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        // Информационная панель
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.add(new JLabel(""));
        add(infoPanel, BorderLayout.NORTH);
    }

    private void encryptText() {
        String text = inputTextArea.getText();
        String key = keyTextField.getText();
        String method = (String) methodComboBox.getSelectedItem();

        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Введите текст для шифрования");
            return;
        }

        try {
            String result;
            switch (method) {
                case "Шифр Цезаря":
                    int shift = Integer.parseInt(key);
                    result = caesarEncrypt(text, shift);
                    break;
                case "Табличный шифр":
                    int size = Integer.parseInt(key);
                    result = tableEncrypt(text, size);
                    break;
                case "Шифр решеткой":
                    result = gridEncrypt(text, key);
                    break;
                default:
                    result = "Неизвестный метод";
            }
            outputTextArea.setText(result);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + ex.getMessage());
        }
    }

    private void decryptText() {
        String text = inputTextArea.getText();
        String key = keyTextField.getText();
        String method = (String) methodComboBox.getSelectedItem();

        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Введите текст для расшифровки");
            return;
        }

        try {
            String result;
            switch (method) {
                case "Шифр Цезаря":
                    int shift = Integer.parseInt(key);
                    result = caesarDecrypt(text, shift);
                    break;
                case "Табличный шифр":
                    int size = Integer.parseInt(key);
                    result = tableDecrypt(text, size);
                    break;
                case "Шифр решеткой":
                    result = gridDecrypt(text, key);
                    break;
                default:
                    result = "Неизвестный метод";
            }
            outputTextArea.setText(result);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + ex.getMessage());
        }
    }

    // Шифр Цезаря
    private String caesarEncrypt(String text, int shift) {
        StringBuilder result = new StringBuilder();

        for (char character : text.toCharArray()) {
            if (Character.isLetter(character)) {
                char base = Character.isLowerCase(character) ? 'a' : 'A';
                character = (char) ((character - base + shift) % 26 + base);
            }
            result.append(character);
        }

        return result.toString();
    }

    private String caesarDecrypt(String text, int shift) {
        return caesarEncrypt(text, 26 - (shift % 26));
    }

    // Табличный шифр (поворотная решетка)
    private String tableEncrypt(String text, int size) {
        // Удаляем пробелы и приводим к верхнему регистру
        text = text.replaceAll("\\s+", "").toUpperCase();

        // Дополняем текст до размера таблицы
        while (text.length() % (size * size) != 0) {
            text += "X";
        }

        StringBuilder result = new StringBuilder();

        for (int block = 0; block < text.length(); block += size * size) {
            String blockText = text.substring(block, block + size * size);
            char[][] table = new char[size][size];

            // Заполняем таблицу
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    table[i][j] = blockText.charAt(i * size + j);
                }
            }

            // Читаем по спирали
            int top = 0, bottom = size - 1, left = 0, right = size - 1;

            while (top <= bottom && left <= right) {
                // Справа налево
                for (int i = right; i >= left; i--) {
                    result.append(table[top][i]);
                }
                top++;

                // Сверху вниз
                for (int i = top; i <= bottom; i++) {
                    result.append(table[i][left]);
                }
                left++;

                // Слева направо
                if (top <= bottom) {
                    for (int i = left; i <= right; i++) {
                        result.append(table[bottom][i]);
                    }
                    bottom--;
                }

                // Снизу вверх
                if (left <= right) {
                    for (int i = bottom; i >= top; i--) {
                        result.append(table[i][right]);
                    }
                    right--;
                }
            }
        }

        return result.toString();
    }

    private String tableDecrypt(String text, int size) {
        StringBuilder result = new StringBuilder();

        for (int block = 0; block < text.length(); block += size * size) {
            String blockText = text.substring(block, block + size * size);
            char[][] table = new char[size][size];

            // Заполняем таблицу в обратном порядке спирали
            int top = 0, bottom = size - 1, left = 0, right = size - 1;
            int index = 0;

            while (top <= bottom && left <= right && index < blockText.length()) {
                // Справа налево (запись)
                for (int i = right; i >= left && index < blockText.length(); i--) {
                    table[top][i] = blockText.charAt(index++);
                }
                top++;

                // Сверху вниз (запись)
                for (int i = top; i <= bottom && index < blockText.length(); i++) {
                    table[i][left] = blockText.charAt(index++);
                }
                left++;

                // Слева направо (запись)
                if (top <= bottom && index < blockText.length()) {
                    for (int i = left; i <= right && index < blockText.length(); i++) {
                        table[bottom][i] = blockText.charAt(index++);
                    }
                    bottom--;
                }

                // Снизу вверх (запись)
                if (left <= right && index < blockText.length()) {
                    for (int i = bottom; i >= top && index < blockText.length(); i--) {
                        table[i][right] = blockText.charAt(index++);
                    }
                    right--;
                }
            }

            // Читаем таблицу построчно
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    result.append(table[i][j]);
                }
            }
        }

        return result.toString();
    }

    // Шифр решеткой (Кардано)
    private String gridEncrypt(String text, String key) {
        // Если ключ - путь к файлу, пытаемся загрузить решетку
        // Иначе создаем случайную решетку 4x4

        int size = 4;
        boolean[][] grid = new boolean[size][size];

        if (new File(key).exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(key))) {
                for (int i = 0; i < size; i++) {
                    String line = reader.readLine();
                    if (line != null) {
                        for (int j = 0; j < size; j++) {
                            grid[i][j] = line.charAt(j) == '1';
                        }
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Ошибка загрузки решетки, создается случайная");
                grid = generateRandomGrid(size);
            }
        } else {
            grid = generateRandomGrid(size);
            // Сохраняем решетку во временный файл
            try {
                saveGridToFile(grid, "grid_pattern.txt");
                keyTextField.setText("grid_pattern.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Приводим текст к нужному формату
        text = text.replaceAll("\\s+", "").toUpperCase();
        while (text.length() % (size * size) != 0) {
            text += "X";
        }

        StringBuilder result = new StringBuilder();

        for (int block = 0; block < text.length(); block += size * size) {
            String blockText = text.substring(block, block + size * size);
            char[][] table = new char[size][size];
            int textIndex = 0;

            // 4 поворота решетки
            for (int rotation = 0; rotation < 4; rotation++) {
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        if (grid[i][j] && textIndex < blockText.length()) {
                            table[i][j] = blockText.charAt(textIndex++);
                        }
                    }
                }
                grid = rotateGrid(grid);
            }

            // Читаем таблицу построчно
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    result.append(table[i][j]);
                }
            }
        }

        return result.toString();
    }

    private String gridDecrypt(String text, String key) {
        int size = 4;
        boolean[][] grid = new boolean[size][size];

        if (new File(key).exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(key))) {
                for (int i = 0; i < size; i++) {
                    String line = reader.readLine();
                    if (line != null) {
                        for (int j = 0; j < size; j++) {
                            grid[i][j] = line.charAt(j) == '1';
                        }
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Ошибка загрузки решетки");
                return "Ошибка: файл решетки не найден";
            }
        } else {
            return "Ошибка: файл решетки не найден";
        }

        StringBuilder result = new StringBuilder();

        for (int block = 0; block < text.length(); block += size * size) {
            String blockText = text.substring(block, block + size * size);
            char[][] table = new char[size][size];

            // Заполняем таблицу из текста
            int idx = 0;
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    table[i][j] = blockText.charAt(idx++);
                }
            }

            // Читаем с использованием решетки
            StringBuilder blockResult = new StringBuilder();
            boolean[][] tempGrid = copyGrid(grid);

            for (int rotation = 0; rotation < 4; rotation++) {
                for (int i = 0; i < size; i++) {
                    for (int j = 0; j < size; j++) {
                        if (tempGrid[i][j]) {
                            blockResult.append(table[i][j]);
                        }
                    }
                }
                tempGrid = rotateGrid(tempGrid);
            }

            result.append(blockResult);
        }

        return result.toString();
    }

    private boolean[][] generateRandomGrid(int size) {
        Random random = new Random();
        boolean[][] grid = new boolean[size][size];
        int holes = size * size / 4;

        for (int i = 0; i < holes; i++) {
            int x, y;
            do {
                x = random.nextInt(size);
                y = random.nextInt(size);
            } while (grid[x][y]);
            grid[x][y] = true;
        }

        return grid;
    }

    private boolean[][] rotateGrid(boolean[][] grid) {
        int size = grid.length;
        boolean[][] rotated = new boolean[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                rotated[j][size - 1 - i] = grid[i][j];
            }
        }

        return rotated;
    }

    private boolean[][] copyGrid(boolean[][] grid) {
        int size = grid.length;
        boolean[][] copy = new boolean[size][size];

        for (int i = 0; i < size; i++) {
            System.arraycopy(grid[i], 0, copy[i], 0, size);
        }

        return copy;
    }

    private void saveGridToFile(boolean[][] grid, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[i].length; j++) {
                    writer.print(grid[i][j] ? "1" : "0");
                }
                writer.println();
            }
        }
    }

    private void loadFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Выберите текстовый файл");

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                inputTextArea.setText(content.toString());

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Ошибка загрузки файла: " + ex.getMessage());
            }
        }
    }

    private void saveToFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Сохранить результат в файл");

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.print(outputTextArea.getText());
                JOptionPane.showMessageDialog(this, "Файл успешно сохранен");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Ошибка сохранения файла: " + ex.getMessage());
            }
        }
    }
}