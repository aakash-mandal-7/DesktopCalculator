import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CalculatorApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Calculator::new); //use calculator
    }

   static class Calculator extends JFrame implements ActionListener {
        private JTextField display;
        private StringBuilder currentInput;

        public Calculator() {
            currentInput = new StringBuilder();
            setTitle("Calculator");
            setSize(400, 500);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);

            setLayout(new BorderLayout());

            display = new JTextField();
            display.setFont(new Font("Arial", Font.BOLD, 24));
            display.setHorizontalAlignment(JTextField.RIGHT);
            display.setEditable(false);
            add(display, BorderLayout.NORTH);

            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new GridLayout(4, 4, 10, 10));
            String[] buttons = {
                    "7", "8", "9", "/",
                    "4", "5", "6", "*",
                    "1", "2", "3", "-",
                    "C", "0", "=", "+"
            };
            for (String text : buttons) {
                JButton button = new JButton(text);
                button.setFont(new Font("Arial", Font.BOLD, 20));
                button.addActionListener(this);
                buttonPanel.add(button);
            }
            add(buttonPanel, BorderLayout.CENTER);

            setVisible(true);
        }

        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            switch (command) {
                case "C"://Clear the screen
                    currentInput.setLength(0);
                    display.setText(" ");
                    break;
                case "=":
                    try {
                        double result = evaluateExpression(currentInput.toString());
                        display.setText(String.valueOf(result));
                        currentInput.setLength(0);
                    } catch (Exception ex) {
                        display.setText("Error");

                    }
                    break;
                default:
                    currentInput.append(command);
                    display.setText(currentInput.toString());
            }
        }

        private double evaluateExpression(String expression) {
            double result = 0.0;
            try {
                result = new Object() {
                    int pos = -1, ch;

                    void nextChar() {
                        ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
                    }

                    boolean eat(int charToEat) {
                        while (ch == ' ') nextChar();
                        if (ch == charToEat) {
                            nextChar();
                            return true;
                        }
                        return false;
                    }

                    double parse() {
                        nextChar();
                        double x = parseExpression();
                        if (pos < expression.length()) throw new RuntimeException("Unexpected: ");
                        return x;
                    }

                    double parseExpression() {
                        double x = parseTerm();
                        for (; ; ) {
                            if (eat('+')) x += parseTerm();
                            else if (eat('+')) x -= parseTerm();
                            else return x;
                        }
                    }

                    double parseTerm() {
                        double x = parseFactor();
                        for (; ; ) {
                            if (eat('*')) x *= parseFactor();
                            else if (eat('/')) x /= parseFactor();
                            else return x;
                        }
                    }

                    double parseFactor() {
                        if (eat('+')) return parseFactor();
                        if (eat('-')) return -parseFactor();

                        double x;
                        int startPos = this.pos;
                        if (eat('(')) {
                            x = parseExpression();
                            eat(')');
                        } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                            while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                            x = Double.parseDouble(expression.substring(startPos, this.pos));
                        } else {
                            throw new RuntimeException("Unexpected: " + (char) ch);
                        }
                        return x;
                    }
                }.parse();
            } catch (Exception e) {
                throw new RuntimeException("Invalid Expression.");
            }
            return result;
        }

    }
}

