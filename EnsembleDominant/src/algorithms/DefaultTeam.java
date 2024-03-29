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
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import algorithms.arbreCouvrant.*;

public class DefaultTeam {
	public ArrayList<Point> calculDominatingSet(ArrayList<Point> points, int edgeThreshold) {
		// REMOVE >>>>>
		ArrayList<Point> result = methodeB(points, edgeThreshold);
		// ArrayList<Point> result = separateurTest3410456(points, edgeThreshold);
		// ArrayList<Point> meilleur = result;
		// for (int i = 0; i < 300; i++) {
		// Collections.shuffle(result);
		// Collections.shuffle(points);
		// // System.out.print(i + " ");
		// // System.out.println(meilleur.size());
		// result = improve(points, result, edgeThreshold);
		//
		// if (meilleur.size() >= result.size()) {
		// meilleur = result;
		// }
		// }
		// result = meilleur;
		// do {
		// meilleur = result;
		// System.out.println(meilleur.size());
		// result = tentaSteiner(points, result, edgeThreshold);
		// } while (meilleur.size() > result.size());
		//
		// // System.out.println(isValid(points, result, edgeThreshold));
		// // System.out.println(result.size());
		// return meilleur;
		ArrayList<Point> courant;
		for (int i = 0; i < 50; i++) {
			courant = methodeB(points, edgeThreshold);
			if (courant.size() < result.size()) {
				result = courant;
			}
		}
		return result;
	}

	public ArrayList<Point> methodeB(ArrayList<Point> points, int edgeThreshold) {
		ArrayList<Point> clonePoints = (ArrayList<Point>) points.clone();
		ArrayList<Point> res = new ArrayList<>();
		int avant;
		Collections.shuffle(clonePoints);
		do {
			do {
				avant = clonePoints.size();
				for (Point p : points) {
					ArrayList<Point> neighbor = neighbor(p, clonePoints, edgeThreshold);
					if (neighbor.size() == 0) {
						if (clonePoints.remove(p)) {
							res.add(p);
						}
					} else {
						if (neighbor.size() == 1) {
							if (clonePoints.remove(p)) {
								Point voisin = neighbor.get(0);
								clonePoints.remove(voisin);
								clonePoints.removeAll(neighbor(voisin, clonePoints, edgeThreshold));
								res.add(voisin);
							}
						}
					}
				}
			} while (avant > clonePoints.size());

			do {
				avant = clonePoints.size();
				for (Point p : points) {
					ArrayList<Point> neighbor = neighbor(p, clonePoints, edgeThreshold);
					if (neighbor.size() == 1) {
						if (clonePoints.remove(p)) {
							Point voisin = neighbor.get(0);
							clonePoints.remove(voisin);
							clonePoints.removeAll(neighbor(voisin, clonePoints, edgeThreshold));
							res.add(voisin);
						}
					}
				}
			} while (avant > clonePoints.size());

			avant = clonePoints.size();
			if (avant == 0) {
				break;
			}
			Collections.shuffle(clonePoints);
			Point min = findMin(clonePoints, edgeThreshold);

			// System.out.println(clonePoints.size());
			int max = 0;
			Point maxVoisin = null;
			for (Point p : neighbor(min, clonePoints, edgeThreshold)) {
				ArrayList<Point> v = neighbor(p, clonePoints, edgeThreshold);
				if (v.size() > max) {
					max = v.size();
					maxVoisin = p;
				}
			}
			if (maxVoisin == null) {
				System.out.println("maxVoisin est null");
				continue;
			} else {
				if (clonePoints.remove(maxVoisin)) {
					clonePoints.removeAll(neighbor(maxVoisin, clonePoints, edgeThreshold));
					res.add(maxVoisin);
				}
			}
		} while (clonePoints.size() > 0);
		ArrayList<Point> meilleur = (ArrayList<Point>) res.clone();

		int i = 0;
		int a = meilleur.size();
		do {
			a = meilleur.size();
			Collections.shuffle(res);
			Collections.shuffle(points);
			System.out.print(i + " ");
			System.out.println(meilleur.size());
			res = improve(points, res, edgeThreshold);
			if (meilleur.size() > res.size()) {
				meilleur = res;
				// i = 0;
			}
			i++;
		} while (a > meilleur.size());
		res = meilleur;
		do {
			Collections.shuffle(meilleur);
			meilleur = res;
			System.out.println(meilleur.size());
			res = tentaSteiner(points, res, edgeThreshold);
		} while (meilleur.size() > res.size());

		return res;
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

	public ArrayList<Point> gloutonPasNaif(ArrayList<Point> points, int edgeThreshold) {
		ArrayList<Point> res = new ArrayList<>();
		@SuppressWarnings("unchecked")
		ArrayList<Point> clonePoints = (ArrayList<Point>) points.clone();
		int avant;
		Collections.shuffle(clonePoints);
		do {
			avant = clonePoints.size();
			for (Point p : points) {
				ArrayList<Point> neighbor = neighbor(p, clonePoints, edgeThreshold);
				if (neighbor.size() == 0) {
					if (clonePoints.remove(p)) {
						res.add(p);
					}
				} else {
					if (neighbor.size() == 1) {
						if (clonePoints.remove(p)) {
							Point voisin = neighbor.get(0);
							clonePoints.remove(voisin);
							clonePoints.removeAll(neighbor(voisin, clonePoints, edgeThreshold));
							res.add(voisin);
						}
					}
				}
			}
		} while (avant > clonePoints.size());
		Collections.shuffle(clonePoints);
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

	public ArrayList<Point> gloutonRandom(ArrayList<Point> points, int edgeThreshold) {
		ArrayList<Point> res = new ArrayList<>();
		@SuppressWarnings("unchecked")
		ArrayList<Point> clonePoints = (ArrayList<Point>) points.clone();

		while (!isValid(points, res, edgeThreshold)) {
			if (clonePoints.size() <= 0) {
				break;
			}
			Random r = new Random();
			int numeroAleatoire = r.nextInt(clonePoints.size());
			Point p = clonePoints.get(numeroAleatoire);

			for (Point n : neighbor(p, points, edgeThreshold)) {
				if (!res.contains(n))
					clonePoints.remove(n);
			}
			clonePoints.remove(p);
			res.add(p);
		}
		return res;
	}

	public ArrayList<Point> tentaSteiner(ArrayList<Point> points, ArrayList<Point> sol, int edgeTreshold) {
		Steiner steiner = new Steiner();
		ArrayList<Point> res = steiner.calculSteiner(points, edgeTreshold, sol);

		ArrayList<Point> voisin = new ArrayList<>();
		for (Point p : res) {
			if (!sol.contains(p)) {
				voisin.add(p);
			}
		}

		return improve(points, sol, voisin, edgeTreshold);
	}

	public ArrayList<Point> improve(ArrayList<Point> points, ArrayList<Point> sol, ArrayList<Point> voisinsInteressant,
			int edgeThreshold) {
		ArrayList<Point> res = (ArrayList<Point>) sol.clone();
		ArrayList<Point> voisins = (ArrayList<Point>) voisinsInteressant.clone();

		int avant = sol.size();
		Collections.shuffle(res);
		for (Point current : sol) {
			Collections.shuffle(res);
			for (Point second : sol) {
				if (current.distance(second) >= edgeThreshold * 2) {
					continue;
				}
				if (current.equals(second))
					continue;
				Collections.shuffle(res);
				Collections.shuffle(voisins);
				for (Point v : voisins) {
					if (v.distance(current) <= edgeThreshold && v.distance(second) <= edgeThreshold) {
						res.remove(current);
						res.remove(second);
						res.add(v);
						if (isValid(points, res, edgeThreshold)) {
							if (res.size() < avant) {
								return res;
							}
						} else {
							res.remove(v);
							res.add(current);
							res.add(second);
						}
					}
				}
			}
		}

		return res;
	}

	public ArrayList<Point> improve(ArrayList<Point> points, ArrayList<Point> sol, int edgeThreshold) {
		ArrayList<Point> res = (ArrayList<Point>) sol.clone();
		int avant = sol.size();
		Collections.shuffle(sol);
		for (Point current : sol) {
			ArrayList<Point> voisin = neighbor(current, points, edgeThreshold);
			if (voisin.size() == 0) {
				continue;
			}
			Point commun = null;
			ArrayList<Point> voisins = new ArrayList<>();
			for (Point second : sol) {
				if (current.distance(second) >= edgeThreshold * 2) {
					continue;
				}
				if (current.equals(second))
					continue;
				for (Point c : voisin) {
					voisins = neighbor(c, points, edgeThreshold);
					if (voisins.size() == 0) {
						continue;
					}
					if (voisins.contains(second)) {
						commun = c;
						break;
					}
				}
				if (commun == null) {
					continue;
				}
				// Collections.shuffle(res);

				// Collections.shuffle(voisins);
				for (Point v : voisins) {
					if (v.distance(current) <= edgeThreshold && v.distance(second) <= edgeThreshold) {
						res.remove(current);
						res.remove(second);
						res.add(v);
						if (isValid(points, res, edgeThreshold)) {
							if (res.size() < avant) {
								return res;
							}
						} else {
							res.remove(v);
							res.add(current);
							res.add(second);
						}
					}
				}
			}
		}
		return res;
	}

	public ArrayList<Point> commonNeigh(Point a, Point b, ArrayList<Point> points, int edgeThreshold) {
		ArrayList<Point> voisinA = neighbor(a, points, edgeThreshold);
		ArrayList<Point> voisinB = neighbor(b, points, edgeThreshold);
		voisinA.retainAll(voisinB);
		return voisinA;
	}

	public ArrayList<Point> separateurTest3410456(ArrayList<Point> points, int edgeThreshold) {
		int x = 0;
		ArrayList<Point> resFinal = new ArrayList<>();
		ArrayList<Point> clone = (ArrayList<Point>) points.clone();
		for (int i = 0; i < points.size(); i++) {
			Point courant = points.get(i);
			x += courant.x;
		}
		if (points.size() > 0) {
			x /= points.size();
		}
		int avant;
		do {
			avant = clone.size();
			for (Point p : points) {
				ArrayList<Point> neighbor = neighbor(p, clone, edgeThreshold);
				if (neighbor.size() == 0) {
					if (clone.remove(p)) {
						resFinal.add(p);
					}
				} else {
					if (neighbor.size() == 1) {
						if (clone.remove(p)) {
							Point voisin = neighbor.get(0);
							clone.remove(voisin);
							clone.removeAll(neighbor(voisin, clone, edgeThreshold));
							resFinal.add(voisin);
						}
					}
				}
			}
		} while (avant > clone.size());
		ArrayList<Point> separateurX = new ArrayList<>();
		for (int i = 0; i < clone.size(); i++) {
			Point courant = clone.get(i);
			if (x - edgeThreshold / 2 <= courant.x && courant.x <= x + edgeThreshold / 2) {
				separateurX.add(courant);
			}
		}
		Collections.shuffle(separateurX);
		if (separateurX.size() > 0) {
			Point p0 = findMax(separateurX, edgeThreshold);
			resFinal.add(p0);
			ArrayList<Point> neighbor0 = neighbor(p0, clone, edgeThreshold);
			Point p1 = null;
			ArrayList<Point> neighbor1 = new ArrayList<Point>();
			for (Point p : separateurX) {
				if (!neighbor0.contains(p) && !p0.equals(p)) {
					p1 = p;
					neighbor1 = neighbor(p1, clone, edgeThreshold);
					resFinal.add(p1);
					clone.remove(p1);
					clone.removeAll(neighbor1);
				}
			}
			clone.remove(p0);
			clone.removeAll(neighbor0);

			ArrayList<Point> cloneG = new ArrayList<>();
			ArrayList<Point> cloneD = new ArrayList<>();
			for (Point p : clone) {
				if (p.x < x - edgeThreshold / 2 || (x - edgeThreshold / 2 < p.x && p.x < x + edgeThreshold / 2)) {
					cloneG.add(p);
				}
				if (x + edgeThreshold / 2 < p.x) {
					cloneD.add(p);
				}
			}
			resFinal.addAll(separateurTest3410456(cloneG, edgeThreshold));
			resFinal.addAll(separateurTest3410456(cloneD, edgeThreshold));
		}
		return resFinal;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Point> separateurTest2(ArrayList<Point> points, int edgeThreshold) {
		ArrayList<Point> resFinal = new ArrayList<>();
		ArrayList<Point> clone = (ArrayList<Point>) points.clone();

		for (Point p : points) {
			if (neighbor(p, clone, edgeThreshold).size() == 0) {
				resFinal.add(p);
				clone.remove(p);
			}
		}
		if (clone.size() == 0) {
			return resFinal;
		}
		if (clone.size() == 1) {
			return clone;
		}
		if (clone.size() == 2) {
			if (clone.get(0).distance(clone.get(1)) <= edgeThreshold) {
				resFinal.add(clone.get(0));
				return resFinal;
			} else {
				return clone;
			}
		}
		int ymax = 0;
		for (Point p : clone) {
			if (p.y > ymax) {
				ymax = p.y;
			}
		}

		for (int y = 0; y < ymax; y = y + edgeThreshold) {
			ArrayList<Point> tmp = new ArrayList<>();
			for (Point p : clone) {
				if (p.y >= y && p.y <= ymax) {
					tmp.add(p);
				}
			}
			if (tmp.size() == 0) {
				continue;
			}
			Point max = tmp.get(0);
			resFinal.add(max);
			clone.remove(max);
			clone.removeAll(neighbor(max, clone, edgeThreshold));
		}

		return resFinal;

		// int m = 0;
		// Point max = clone.get(0);
		// for (Point p : clone) {
		// if (neighbor(p, clone, edgeThreshold).size() > m) {
		// m = neighbor(p, clone, edgeThreshold).size();
		// max = p;
		// }
		// }
		//
		// resFinal.add(max);
		//
		// clone.remove(max);
		// clone.removeAll(neighbor(max, clone, edgeThreshold));
		//
		// ArrayList<Point> gauche = new ArrayList<>();
		// ArrayList<Point> droite = new ArrayList<>();
		//
		// int g = max.x;
		//
		// for (Point p : clone) {
		// if (p.x < g) {
		// gauche.add(p);
		// } else {
		// droite.add(p);
		// }
		// }
		//
		// resFinal.addAll(separateurTest2(gauche, edgeThreshold));
		// resFinal.addAll(separateurTest2(droite, edgeThreshold));
		//
		// return resFinal;
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
		// y /= separateurX.size();
		// ArrayList<Point> separateurY = new ArrayList<>();
		// for (int i = 0; i < points.size(); i++) {
		// Point courant = points.get(i);
		// if (y - edgeThreshold / 2 <= courant.y && courant.y <= y + edgeThreshold / 2)
		// {
		// separateurY.add(courant);
		// }
		// }

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
			if (res == null) {
				return new ArrayList<Point>();
			} else {
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

	@SuppressWarnings("unchecked")
	public ArrayList<Point> separateurTest(ArrayList<Point> points, int edgeThreshold) {

		int x = 0;
		int y = 0;
		ArrayList<Point> resFinal = new ArrayList<>();
		ArrayList<Point> clone = (ArrayList<Point>) points.clone();

		for (Point p : points) {
			if (neighbor(p, clone, edgeThreshold).size() == 0) {
				resFinal.add(p);
				clone.remove(p);
			}
		}
		if (clone.size() == 0) {
			return resFinal;
		}
		if (clone.size() == 1) {
			return clone;
		}
		if (clone.size() == 2) {
			if (clone.get(0).distance(clone.get(1)) <= edgeThreshold) {
				resFinal.add(clone.get(0));
				return resFinal;
			} else {
				return clone;
			}
		}

		for (int i = 0; i < clone.size(); i++) {
			Point courant = clone.get(i);
			x += courant.x;
		}
		x = x / clone.size();

		ArrayList<Point> separateurX = new ArrayList<>();

		for (int i = 0; i < clone.size(); i++) {
			Point courant = clone.get(i);
			if (x - (edgeThreshold / 2) <= courant.x && courant.x <= x + (edgeThreshold / 2)) {
				separateurX.add(courant);
				y += courant.y;
			}
		}
		if (separateurX.size() <= 0) {
			System.out.println("PAS BON");
			return null;
		}

		y = y / separateurX.size();

		ArrayList<Point> separateurY = new ArrayList<>();
		for (int i = 0; i < clone.size(); i++) {
			Point courant = clone.get(i);
			if (y - edgeThreshold / 2 <= courant.y && courant.y <= y + edgeThreshold / 2) {
				separateurY.add(courant);
			}
		}

		if (separateurY.size() == 0 && clone.size() > 0) {
			return gloutonNaif(clone, edgeThreshold);
		}

		if (separateurY.size() > 2) {
			int max = 0;
			Point sep1 = separateurY.get(0);
			for (Point p : separateurY) {
				if (neighbor(p, clone, edgeThreshold).size() > max) {
					max = neighbor(p, clone, edgeThreshold).size();
					sep1 = p;
				}
			}
			max = 0;
			Point sep2 = null;

			ArrayList<Point> neighborSep1 = neighbor(sep1, separateurY, edgeThreshold);
			ArrayList<Point> notNeighOfSepInY = new ArrayList<>();

			for (Point p : neighborSep1) {
				if (!separateurY.contains(p)) {
					notNeighOfSepInY.add(p);
				}
			}
			if (notNeighOfSepInY.size() != 0) {
				sep2 = notNeighOfSepInY.get(0);
				for (Point p : notNeighOfSepInY) {
					if (neighbor(p, clone, edgeThreshold).size() > max) {
						max = neighbor(p, clone, edgeThreshold).size();
						sep2 = p;
					}
				}
				resFinal.add(sep1);
				resFinal.add(sep2);

				clone.remove(sep1);
				clone.removeAll(neighbor(sep1, clone, edgeThreshold));

				clone.remove(sep2);
				clone.removeAll(neighbor(sep2, clone, edgeThreshold));

				ArrayList<Point> gauche = new ArrayList<>();
				ArrayList<Point> droite = new ArrayList<>();
				int g = 0;
				// int d = 0;
				if (sep1.x <= sep2.x) {
					g = sep1.x;
					// d = sep2.x;
				} else {
					g = sep2.x;
					// d = sep1.x;
				}

				for (Point p : clone) {
					if (p.x < g) {
						gauche.add(p);
					} else {
						droite.add(p);
					}
				}
				resFinal.addAll(separateurTest(gauche, edgeThreshold));
				resFinal.addAll(separateurTest(droite, edgeThreshold));

			} else {

				resFinal.add(sep1);
				System.out.println(clone.size());
				clone.remove(sep1);
				clone.removeAll(neighbor(sep1, clone, edgeThreshold));
				System.out.println(clone.size());
				ArrayList<Point> gauche = new ArrayList<>();
				ArrayList<Point> droite = new ArrayList<>();
				int g = sep1.x;

				for (Point p : clone) {
					if (p.x < g) {
						gauche.add(p);
					} else {
						droite.add(p);
					}
				}
				resFinal.addAll(separateurTest(gauche, edgeThreshold));
				resFinal.addAll(separateurTest(droite, edgeThreshold));
			}

		}

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
		ArrayList<Point> result = (ArrayList<Point>) points.clone();

		for (Point p : sol) {
			result.remove(p);
			result.removeAll(neighbor(p, points, edgeThreshold));
		}
		return result.size() == 0;
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
