import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    static int C, P;
    static int [] map;

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        StringTokenizer st = new StringTokenizer(br.readLine());
        C = Integer.parseInt(st.nextToken());
        P = Integer.parseInt(st.nextToken());
        map = new int[C];

        st = new StringTokenizer(br.readLine());
        for (int i = 0; i < C; ++i) {
            map[i] = Integer.parseInt(st.nextToken());
        }

        System.out.println("@@@@@");
        System.out.println(solve());
        System.out.println("@@@@@");
    }

    private static int solve() {
        int ret = 0;
        if (P == 1) {
            ret += C;

            for (int i = 0; i <= C - 4; ++i) {
                ret += is_level(i, 4);
            }

        } else if (P == 2) {    // 정사각형

            for (int i = 0; i <= C - 2; ++i) {
                ret += is_level(i, 2);
            }
            
        } else if (P == 3) { // 오른쪽을 보고 있는 지그재그

            for (int i = 0; i <= C - 3; ++i) {
                if (map[i] == map[i + 1] && map[i] == map[i + 2] - 1) ret++;
            }

            for (int i = 0; i <= C - 2; ++i) {
                if (map[i] == map[i + 1] + 1) ret++;
            }

        } else if (P == 4) {

            for (int i = 0; i <= C - 3; ++i) {
                if (map[i + 1] == map[i + 2] && map[i] - 1 == map[i + 1]) ret++;
            }

            for (int i = 0; i <= C - 2; ++i) {
                if (map[i] == map[i + 1] - 1) ret++;
            }

        } else if (P == 5) {

            // ㅗ
            for (int i = 0; i <= C - 3; ++i) {
                ret += is_level(i, 3);
            }

            // ㅏ
            for (int i = 0; i <= C - 2; ++i) {
                if (map[i] == map[i + 1] - 1) ret++;
            }

            // ㅜ
            for (int i = 0; i <= C - 3; ++i) {
                if (map[i] == map[i + 2] && map[i + 1] == map[i] - 1) ret++;
            }

            // ㅓ
            for (int i = 0; i <= C - 2; ++i) {
                if (map[i] == map[i + 1] + 1) ret++;
            }

        } else  if (P == 6) {

            for (int i = 0; i <= C - 3; ++i) {
                ret += is_level(i, 3);
            }

            for (int i = 0; i <= C - 2; ++i) {
                ret += is_level(i, 2);
            }

            for (int i = 0; i <= C - 3; ++i) {
                if (map[i] == map[i + 1] - 1 && map[i + 1] == map[i + 2]) ret++;
            }

            for (int i = 0; i <= C - 2; ++i) {
                if (map[i] - 2 == map[i + 1]) ret++;
            }

        } else if (P == 7) {

            for (int i = 0; i <= C - 3; ++i) {
                ret += is_level(i, 3);
            }

            for (int i = 0; i <= C - 2; ++i) {
                ret += is_level(i, 2);
            }

            for (int i = 0; i < C - 3; ++i) {
                if (map[i] == map[i + 1] && map[i + 1] == map[i + 2] + 1) ret++;
            }

            for (int i = 0; i <= C - 2; ++i) {
                if (map[i] + 2 == map[i + 1]) ret++;
            }
        }

        return ret;
    }

    private static int is_level(int s, int c) {
        int h = map[s];
        for (int i = s + 1; i < s + c; ++i) {
            if (h != map[i]) return 0;
        }
        return 1;
    }

}