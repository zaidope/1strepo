import java.util.Stack;

public class ValidParentheses {
    public static boolean isValid(String s) {
        Stack<Character> stack = new Stack<>();
        
        for (char c : s.toCharArray()) {
            if (c == '(' || c == '{' || c == '[') {
                stack.push(c);  // Push opening brackets
            } else {
                if (stack.isEmpty()) return false;  // No matching opening bracket
                
                char top = stack.pop();  // Pop the last opening bracket
                if ((c == ')' && top != '(') || 
                    (c == '}' && top != '{') || 
                    (c == ']' && top != '[')) {
                    return false;  // Mismatch case
                }
            }
        }
        
        return stack.isEmpty();  // Stack should be empty if valid
    }

    public static void main(String[] args) {
        System.out.println(isValid("()"));       // true
        System.out.println(isValid("()[]{}"));   // true
        System.out.println(isValid("(]"));       // false
        System.out.println(isValid("{[]}"));     // true
        System.out.println(isValid("([)]"));     // false
    }
}
