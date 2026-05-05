import java.util.*;

public class In {
    private static Scanner in = new Scanner(System.in);

    public static String nextLine() {
        return in.nextLine();
    }

    public static String nextChar() {
        return in.nextLine().substring(0, 1);
    }

    public static String nextUpperChar() {
        return in.nextLine().toUpperCase().substring(0, 1);
    }

    public static int nextInt() {
        int i = in.nextInt();
        in.nextLine();
        return i;
    }

    public static double nextDouble() {
        double d = in.nextDouble();
        in.nextLine();
        return d;
    }
}
