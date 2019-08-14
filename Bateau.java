// When we create an abstract class, we are creating a
// base class that might have one or more completed
// methods but at least one or more methods are left
//  uncompleted and declared abstract. If all the
//  methods of an abstract class are uncompleted then
//  it is same as an interface. The purpose of an
//   abstract class is to provide a base class
// 	definition for how a set of derived classes
// 	will work and then allow the programmers to
// 	fill the implementation in the derived classes.

import java.util.Scanner;

public abstract class Bateau	{ //rendre abstraite + add methods

	public static int resistance;
	public static int munitions;
	public static int taille;
	public Orientation orientation;
	//attaque ?

	private Position tete;
	private Position[] emplacements;

	public enum Orientation	{
		Horizontal,
		Vertical,
		Diagonal,
		AntiDiagonal;
	}

	//public abstract void tirer();
	public abstract int getTaille();
	public abstract int getResistance();
	public abstract Position[] getEmplacements();
	public abstract Orientation getOrientation();
	public abstract Position getTete();

	public abstract void setTete(Position pos);
	public abstract void setOrientation(Orientation o);
	public abstract void setEmplacements(int index, Position pos);
	public abstract void setAvancee(int index, Orientation o);
	public abstract void setReculee(int index, Orientation o);

	public void fillBoat()	{	// position de départ
		this.setEmplacements(0, this.getTete());
		for (int i = 1; i < this.getTaille(); i++)	{
			this.setEmplacements(i, new Position(this.getEmplacements()[i-1]));
														// evite de pointer sur la m position
			this.setAvancee(i, this.getOrientation()); // avance selon l'orientation
		}
	}

	public void randomizePosition()	{
		double yAleatoire =  Math.random() * Globals.getLongueurChampMax() + 1;
		double xAleatoire =  Math.random() * Globals.getLongueurChampMax() + 1;
		int x = (int) xAleatoire;
		int y = (int) yAleatoire;
		this.setTete(new Position((x-1),(y-1)));
		System.out.println((x-1) +"and"+ (y-1));
	}


	public void setRandomOrientation()	{
		double nbAleatoire =  Math.random() * 4 + 1;
		switch ((int) nbAleatoire)	{
			case 1:
			this.setOrientation(Orientation.Horizontal);
			break;
			case 2:
			this.setOrientation(Orientation.Vertical);
			break;
			case 3:
			this.setOrientation(Orientation.AntiDiagonal);
			break;
			case 4:
			this.setOrientation(Orientation.Diagonal);
			break;
		}
	}

	public boolean isInside()	{
		boolean inside = true;
		for (int i = 0; i < this.getTaille(); i++)	{
			System.out.println(this.getEmplacements()[1]); //at the end getX a changer
			if ((this.getEmplacements()[i].getX() > Globals.getLongueurChampMax()-1 ||
			 			this.getEmplacements()[i].getX() < 0) ||
					(this.getEmplacements()[i].getY() > Globals.getLongueurChampMax()-1 ||
						this.getEmplacements()[i].getY() < 0))	{
				inside = false;
			}
		}
		return inside;
	}

	public boolean touchesA(Bateau[] bateaux)	{
		boolean isHit = false;
		for (int i = 0; i < bateaux.length; i++) {
			if (this != bateaux[i])	{ //pas de equals ici car on compare les adresses
				if (this.touches(bateaux[i]))	{
					System.out.println("le boat is hit !");
					isHit = true;
				}
			}
		}
		return (isHit);
	}

	public boolean touches(Bateau bateau)	{
		boolean isHit = false;
		for (int i = 0; i < this.getEmplacements().length; i++) {
			for (int j = 0; j < bateau.getEmplacements().length; j++)	{
				if (this.getEmplacements()[i].equals(bateau.getEmplacements()[j]))	{
					System.out.println("le position is hit !");
					isHit = true;
				}
			}
		}
		return (isHit);
	}

	public void avancer()	{
		for (int i = 0; i < this.getEmplacements().length; i++) {
			this.setAvancee(i, this.getOrientation());
		}
	}

	public void reculer()	{
		for (int i = 0; i < this.getEmplacements().length; i++) {
			this.setReculee(i, this.getOrientation());
		}
	}

	public void tirer(ChampBataille cb)	{

		System.out.println("Dans quelle direction ?\n1. Nord\n2. Nord-Est\n3. Est\n4. Sud-Est\n5. Sud\n6. Sud-Ouest\n7. Ouest\n8. Nord-Ouest");
		Scanner sc = new Scanner(System.in);
		Position pos = this.getEmplacements()[0];
		int choix = sc.nextInt();
		int[][] t = cb.getT();
		int id = 0;
		int j = pos.getY();
		int i = pos.getX();
		System.out.println("pos initiale "+pos);
		int max = t[i].length;

		switch (choix) {
			case 1:
				while (i > 0) { //avancee N
					i--;
					id = t[i][j];
					System.out.println((i)+";"+(j));
					if (id!=0)	{
						break;
					}
				}
				break;

			case 2:
				while (i > 0 && j < max) { //avancee NE
					i--;
					j++;
					id = t[i][j];
					System.out.println((i)+";"+(j));
					if (id!=0)	{
						break;
					}
				}
				break;

			case 3:
				while (j < max) { //avancee E
					j++;
					id = t[i][j];
					System.out.println((i)+";"+(j));
					if (id!=0)	{
						break;
					}
				}
				break;

			case 4:
				while (i < t.length && j < max) { //avancee SE
					i++;
					j++;
					id = t[i][j];
					System.out.println((i)+";"+(j));
					if (id!=0)	{
						break;
					}
				}
				break;

			case 5:
				while (i < t.length) { //avancee S
					i++;
					id = t[i][j];
					System.out.println((i)+";"+(j));
					if (id!=0)	{
						break;
					}
				}
				break;

			case 6:
				while (i < t.length && j > 0) { //avancee SO
					i++;
					j--;
					id = t[i][j];
					System.out.println((i)+";"+(j));
					if (id!=0)	{
						break;
					}
				}
				break;

			case 7:
				while (j > 0) { //avancee O
					j--;
					id = t[i][j];
					System.out.println((i)+";"+(j));
					if (id!=0)	{
						break;
					}
				}
				break;

			case 8:
				while (i > 0 && j > 0) { //avancee NO
					i--;
					j--;
					id = t[i][j];
					System.out.println((i)+";"+(j));
					if (id!=0)	{
						break;
					}
				}
				break;
		}

		switch (id)  {
			case 0:
			System.out.println("rien de touché commandant !");
			break;
			case 1:
			System.out.println("F1 touché");
			break;
			case 2:
			System.out.println("F2 touché");
			break;
			case 3:
			System.out.println("C1 touché");
			break;
			case 4:
			System.out.println("C2 touché");
			break;
			case 5:
			System.out.println("S1 touché");
			break;
			case 6:
			System.out.println("S2 touché");
			break;
		}
		System.out.println("aux coordonnees "+i+";"+j);
	}
}
