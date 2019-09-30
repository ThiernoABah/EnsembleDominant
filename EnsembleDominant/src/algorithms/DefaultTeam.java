package algorithms;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DefaultTeam {
	public ArrayList<Point> calculDominatingSet(ArrayList<Point> points, int edgeThreshold) {
		// REMOVE >>>>>
		// ArrayList<Point> result = gloutonNaif(points, edgeThreshold);

		ArrayList<Point> result = separateur(points, edgeThreshold);
		// System.out.println(isValid(points, result, edgeThreshold));
		// System.out.println(result.size());
		return result;
	}

	public ArrayList<Point> gloutonNaif(ArrayList<Point> points, int edgeThreshold) {
		ArrayList<Point> res = new ArrayList<>();
		@SuppressWarnings("unchecked")
		ArrayList<Point> clonePoints = (ArrayList<Point>) points.clone();

		while (!isValid(points, res, edgeThreshold)) {
			if (clonePoints.size() <= 0) {
				break;
			}
			Point p = findMax(clonePoints, edgeThreshold);

			for (Point n : neighbor(p, points, edgeThreshold)) {
				if (!res.contains(n))
					clonePoints.remove(n);
			}
			clonePoints.remove(p);
			res.add(p);
		}
		return res;
	}

	public ArrayList<Point> separateur(ArrayList<Point> points, int edgeThreshold) {
		int x = 0;
		int y = 0;
		ArrayList<Point> resFinal = new ArrayList<>();
		ArrayList<Point> clone = (ArrayList<Point>) points.clone();
		for (int i = 0; i < points.size(); i++) {
			Point courant = points.get(i);
			x += courant.x;
		}
		x /= points.size();
		ArrayList<Point> separateurY = new ArrayList<>();
		for (int i = 0; i < points.size(); i++) {
			Point courant = points.get(i);
			if (x - edgeThreshold / 2 <= courant.x && courant.x <= x + edgeThreshold / 2) {
				separateurY.add(courant);
				y += courant.y;
			}
		}
		if (separateurY.size() <= 0) {
			System.out.println("PAS BON");
			return null;
		}
//		y /= separateurX.size();
//		ArrayList<Point> separateurY = new ArrayList<>();
//		for (int i = 0; i < points.size(); i++) {
//			Point courant = points.get(i);
//			if (y - edgeThreshold / 2 <= courant.y && courant.y <= y + edgeThreshold / 2) {
//				separateurY.add(courant);
//			}
//		}

		Point sep1 = null;
		Point sep2 = null;

		if (separateurY.size() > 2) {
			sep1 = findMax(separateurY, edgeThreshold);

			ArrayList<Point> neighborSep1 = neighbor(sep1, separateurY, edgeThreshold);

			if (neighborSep1.size() == 0) {
				sep2 = findMax(separateurY, edgeThreshold);
				resFinal.add(sep1);
				resFinal.add(sep2);

				clone.remove(sep1);
				clone.remove(sep2);
				clone.removeAll(neighbor(sep1, clone, edgeThreshold));
				clone.removeAll(neighbor(sep2, clone, edgeThreshold));
				ArrayList<Point> res = separateur(clone, edgeThreshold);
				resFinal.addAll(res);
			} else {
				separateurY.removeAll(neighborSep1);
				if (separateurY.size() > 0) {
					sep2 = findMax(separateurY, edgeThreshold);
					resFinal.add(sep1);
					clone.remove(sep1);
					clone.removeAll(neighbor(sep1, clone, edgeThreshold));

					ArrayList<Point> res = separateur(clone, edgeThreshold);
					resFinal.addAll(res);
				} else {
					resFinal.add(sep1);
					clone.remove(sep1);
					clone.removeAll(neighbor(sep1, clone, edgeThreshold));
					ArrayList<Point> res = separateur(clone, edgeThreshold);
					resFinal.addAll(res);
				}
			}
		} else {
			if (separateurY.size() == 0) {
				return resFinal;
			}
			resFinal.add(separateurY.get(0));
			clone.remove(separateurY.get(0));
			clone.removeAll(neighbor(separateurY.get(0), clone, edgeThreshold));
			ArrayList<Point> res = separateur(clone, edgeThreshold);
			if(res == null) {
				return new ArrayList<Point>();
			}
			else {
				resFinal.addAll(res);
			}
			
		}

		// if (sep1 != null && sep2 != null) {
		// if (neighbor(sep1, points, edgeThreshold).contains(sep2)) {
		// System.out.println("in progress");
		// res.add(sep1);
		// ArrayList<Point> v1 = neighbor(sep1, points, edgeThreshold);
		// clone.removeAll(v1);
		// res.addAll(gloutonNaif(clone, edgeThreshold));
		// for (Point r : res) {
		// resFinal.add(r);
		// }
		// return resFinal;
		// } else {
		// res.add(sep1);
		// res.add(sep2);
		// ArrayList<Point> v1 = neighbor(sep1, points, edgeThreshold);
		// ArrayList<Point> v2 = neighbor(sep2, points, edgeThreshold);
		// clone.removeAll(v1);
		// clone.removeAll(v2);
		// res.addAll(gloutonNaif(clone, edgeThreshold));
		// for (Point r : res) {
		// resFinal.add(r);
		// }
		// return resFinal;
		// }
		// }
		return resFinal;
	}

	public Point findMax(ArrayList<Point> graphe, int edgeThreshold) {
		int max = 0;
		Point res = graphe.get(0);
		for (Point p : graphe) {
			if (neighbor(p, graphe, edgeThreshold).size() > max) {
				max = neighbor(p, graphe, edgeThreshold).size();
				res = p;
			}
		}
		return res;
	}

	public Point findMin(ArrayList<Point> graphe, int edgeThreshold) {
		int min = Integer.MAX_VALUE;
		Point res = graphe.get(0);
		for (Point p : graphe) {
			if (neighbor(p, graphe, edgeThreshold).size() < min) {
				min = neighbor(p, graphe, edgeThreshold).size();
				res = p;
			}
		}
		return res;
	}

	public boolean isValid(ArrayList<Point> points, ArrayList<Point> sol, int edgeThreshold) {
		if (sol.size() == 0) {
			return false;
		}

		Set<Point> res = new HashSet<>();
		for (Point p : points) {
			for (Point n : neighbor(p, points, edgeThreshold)) {
				if (!sol.contains(n))
					res.add(n);
			}
		}
		return res.size() + sol.size() == points.size();
	}

	public int score(ArrayList<Point> points) {
		return points.size();
	}

	public ArrayList<Point> neighbor(Point p, ArrayList<Point> vertices, int edgeThreshold) {
		ArrayList<Point> result = new ArrayList<Point>();
		for (Point point : vertices)
			if (point.distance(p) < edgeThreshold && !point.equals(p))
				result.add((Point) point.clone());
		return result;
	}

	// FILE PRINTER
	private void saveToFile(String filename, ArrayList<Point> result) {
		int index = 0;
		try {
			while (true) {
				BufferedReader input = new BufferedReader(
						new InputStreamReader(new FileInputStream(filename + Integer.toString(index) + ".points")));
				try {
					input.close();
				} catch (IOException e) {
					System.err.println(
							"I/O exception: unable to close " + filename + Integer.toString(index) + ".points");
				}
				index++;
			}
		} catch (FileNotFoundException e) {
			printToFile(filename + Integer.toString(index) + ".points", result);
		}
	}

	private void printToFile(String filename, ArrayList<Point> points) {
		try {
			PrintStream output = new PrintStream(new FileOutputStream(filename));
			int x, y;
			for (Point p : points)
				output.println(Integer.toString((int) p.getX()) + " " + Integer.toString((int) p.getY()));
			output.close();
		} catch (FileNotFoundException e) {
			System.err.println("I/O exception: unable to create " + filename);
		}
	}

	// FILE LOADER
	private ArrayList<Point> readFromFile(String filename) {
		String line;
		String[] coordinates;
		ArrayList<Point> points = new ArrayList<Point>();
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
			try {
				while ((line = input.readLine()) != null) {
					coordinates = line.split("\\s+");
					points.add(new Point(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1])));
				}
			} catch (IOException e) {
				System.err.println("Exception: interrupted I/O.");
			} finally {
				try {
					input.close();
				} catch (IOException e) {
					System.err.println("I/O exception: unable to close " + filename);
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println("Input file not found.");
		}
		return points;
	}
}
