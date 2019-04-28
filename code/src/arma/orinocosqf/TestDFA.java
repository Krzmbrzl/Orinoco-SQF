package arma.orinocosqf;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author K
 * @since 4/27/19
 */
public class TestDFA {
	public static void main(String[] args) {
		OrinocoDFA dfa = OrinocoDFA.SQF.INSTANCE;
		List<OrinocoDFA.Node> current = new ArrayList<>(16);
		List<OrinocoDFA.Node> newCurrent = new ArrayList<>(16);
		current.add(dfa.root);
		Scanner scan = new Scanner(System.in);
		List<OrinocoDFA.Node> visited = new ArrayList<>();

		while (true) {
			String s = scan.nextLine() + "\n";
			if (s.equals("exit")) {
				break;
			}
			boolean setToRoot = false;
			int i = 0;
			for (; i < s.length(); ) {
				System.out.println(s.charAt(i));
				boolean end = false;
				for (OrinocoDFA.Node cursor : current) {
					List<OrinocoDFA.Node> nodes = cursor.next(s.charAt(i));
					if (nodes.isEmpty()) {
						continue;
					}
					if (nodes.size() == 1 && nodes.get(0) == OrinocoDFA.END) {
						setToRoot = true;
						end = true;
						break;
					}
					visited.add(cursor);
					newCurrent.addAll(nodes);
				}
				if (newCurrent.isEmpty() && !end) {
					System.out.print("Illegal Character:'" + s.charAt(i) + "' -- ");
					setToRoot = true;
				}

				current.clear();
				if (setToRoot) {
					System.out.println("DONE");
					System.out.println(visited);
					newCurrent.clear();
					current.add(dfa.root);
					visited.clear();
				} else {
					current.addAll(newCurrent);
					newCurrent.clear();
				}
				if (!end) {
					i++;
				}
			}

		}
	}
}
