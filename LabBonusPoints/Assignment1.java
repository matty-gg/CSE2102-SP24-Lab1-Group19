import java.util.Scanner;
import java.util.Arrays;
import java.lang.Integer;

public class Assignment1 {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter the expression (no blank, no decimals): ");
		String user_input = scanner.nextLine();

		Expression exp = new Expression(user_input);
		double result = exp.calc.evaluate_expression(exp);

		System.out.println("The result is: " + result);
		scanner.close();
	}
}

class NumStorage {
	public double number[];
	public int top;
	public NumStorage(){
		this.number = new double[100];
		this.top = -1;
	}
}

class SymbolStorage {
	public char symbol[];
	public int top;
	public SymbolStorage(){
		this.symbol = new char[100];
		for (int i = 0; i < 100; i++){
			this.symbol[i] = Character.MIN_VALUE;
		}
		this.top = -1;
	}
}

class Expression {
	public NumStorage numStorage;
	public SymbolStorage symbolStorage;
	public String value;
	public Calculator calc = new Calculator();

	public Expression(String user_input) {
		this.numStorage = new NumStorage();
		this.symbolStorage = new SymbolStorage();
		this.value = user_input;
	}

	public double evaluate_expression() {
		return calc.evaluate_expression(this);
	}
}

class Calculator {
	public Calculator(){
		
	}

	public void init_operate_num(NumStorage num_stack) {
		num_stack.top = -1;
	}

	public void init_operate_symbol(SymbolStorage sym_stack) {
		sym_stack.top = -1;
	}

	public void in_num_storage(NumStorage num_stack, double number) {
		num_stack.top += 1;
		num_stack.number[num_stack.top] = number;
	}

	public void in_symbol_storage(SymbolStorage sym_stack, char character) {
		sym_stack.top += 1;
		sym_stack.symbol[sym_stack.top] = character;
	}

	public double read_num_storage(NumStorage num_stack) {
		return num_stack.number[num_stack.top];
	}

	public char read_symbol_storage(SymbolStorage sym_stack) {
		return sym_stack.symbol[sym_stack.top];
	}

	public double get_num_data(NumStorage num_stack) {
		double num = num_stack.number[num_stack.top];
		num_stack.top -= 1;
		return num;
	}

	public char get_symbol(SymbolStorage sym_stack) {
		char c = sym_stack.symbol[sym_stack.top];
		sym_stack.top -= 1;
		return c;
	}

	public int judge_symbol_priority(char c) {
		if (c == '(')
			return 1;
		if (c == '+' || c == '-')
			return 2;
		if (c == '*' || c == '/')
			return 3;
		if (c == ')')
			return 4;
		return 0;
	}

	public double math(double v1, double v2, char c) {
		if (c == '+')
			return (v1 + v2);
		if (c == '-')
			return (v1 - v2);
		if (c == '*')
			return (v1 * v2);
		if (c == '/')
			return (v1 / v2);
		return 0;
	}

	public double evaluate_expression(Expression exp) {
		String user_input = exp.value;
		int t = 0;
		double sum_val = 0;
		double v1, v2 = 0;
		char[] v = new char[100];
		

		for (int i = 0; i < user_input.length(); i++){
			if (i == 0 && user_input.charAt(i) == '-') {
				v[t] = user_input.charAt(i);
				t += 1;
			} else if (user_input.charAt(i) == '(' && i + 1 < user_input.length() && user_input.charAt(i + 1) == '-') {
				i += 1;
				v[t] = user_input.charAt(i);
				t += 1;
				while (i < user_input.length() && user_input.charAt(i) >= '0' && user_input.charAt(i) <= '9') {
					v[t] = user_input.charAt(i);
					t += 1;
					i += 1;
				}
				String s = new String(Arrays.copyOfRange(v, 0, t));
				in_num_storage(exp.numStorage, (double)Integer.parseInt(s));
				while (t > 0) {
					v[t] = Character.MIN_VALUE;
					t -= 1;
				}
				if (i < user_input.length() && user_input.charAt(i) != ')') {
					i -= 1;
					in_symbol_storage(exp.symbolStorage, '(');
				}
			} else if (i < user_input.length() && user_input.charAt(i) >= '0' && user_input.charAt(i) <= '9') {
				while (i < user_input.length() && user_input.charAt(i) >= '0' && user_input.charAt(i) <= '9') {
					v[t] = user_input.charAt(i);
					t += 1;
					i += 1;
				}
				String s = new String(Arrays.copyOfRange(v, 0, t));
				in_num_storage(exp.numStorage, (double)Integer.parseInt(s));
				while (t > 0) {
					v[t] = Character.MIN_VALUE;
					t -= 1;
				}
				i -= 1;
			} else {
				if (exp.symbolStorage.top == -1)
					in_symbol_storage(exp.symbolStorage, user_input.charAt(i));
				else if (judge_symbol_priority(user_input.charAt(i)) == 1)
					in_symbol_storage(exp.symbolStorage, user_input.charAt(i));
				else if (judge_symbol_priority(user_input.charAt(i)) == 2){
					if (judge_symbol_priority(read_symbol_storage(exp.symbolStorage)) == 1)
						in_symbol_storage(exp.symbolStorage, user_input.charAt(i));
					else if (judge_symbol_priority(read_symbol_storage(exp.symbolStorage)) == 2) {
						while (exp.symbolStorage.top >= 0 && exp.numStorage.top >= 1) {
							v2 = get_num_data(exp.numStorage);
							v1 = get_num_data(exp.numStorage);
							sum_val = math(v1, v2, get_symbol(exp.symbolStorage));
							in_num_storage(exp.numStorage, sum_val);
						}
						in_symbol_storage(exp.symbolStorage, user_input.charAt(i));
					}
					else if (judge_symbol_priority(read_symbol_storage(exp.symbolStorage)) == 3) {
						while (exp.symbolStorage.top >= 0 && exp.numStorage.top >= 1) {
							v2 = get_num_data(exp.numStorage);
							v1 = get_num_data(exp.numStorage);
							sum_val = math(v1, v2, get_symbol(exp.symbolStorage));
							in_num_storage(exp.numStorage, sum_val);
						}
						in_symbol_storage(exp.symbolStorage, user_input.charAt(i));
					}
				}
				else if (judge_symbol_priority(user_input.charAt(i)) == 3) {
					if (judge_symbol_priority(read_symbol_storage(exp.symbolStorage)) == 1)
						in_symbol_storage(exp.symbolStorage, user_input.charAt(i));
					else if (judge_symbol_priority(read_symbol_storage(exp.symbolStorage)) == 2)
						in_symbol_storage(exp.symbolStorage, user_input.charAt(i));
					else if (judge_symbol_priority(read_symbol_storage(exp.symbolStorage)) == 3) {
						while (exp.symbolStorage.top >= 0 && exp.numStorage.top >= 1) {
							v2 = get_num_data(exp.numStorage);
							v1 = get_num_data(exp.numStorage);
							sum_val = math(v1, v2, get_symbol(exp.symbolStorage));
							in_num_storage(exp.numStorage, sum_val);
						}
						in_symbol_storage(exp.symbolStorage, user_input.charAt(i));
					}
				}
				else if (judge_symbol_priority(user_input.charAt(i)) == 4) {
					while (exp.symbolStorage.top >= 0 && judge_symbol_priority(read_symbol_storage(exp.symbolStorage)) != 1) {
						v2 = get_num_data(exp.numStorage);
						v1 = get_num_data(exp.numStorage);
						sum_val = math(v1, v2, get_symbol(exp.symbolStorage));
						in_num_storage(exp.numStorage, sum_val);
					}
					get_symbol(exp.symbolStorage);
				}
			}
		}
		while (exp.symbolStorage.top != -1) {
			v2 = get_num_data(exp.numStorage);
			v1 = get_num_data(exp.numStorage);
			sum_val = math(v1, v2, get_symbol(exp.symbolStorage));
			in_num_storage(exp.numStorage, sum_val);
		}
		return exp.numStorage.number[0];
	}
}
