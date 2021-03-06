import java.util.Scanner;

public abstract class Bateau	{

	private int resistance;
	private int munitions;
	public static int taille;
	public static int degats;
	public Orientation orientation;


	private Position tete;
	private Position[] emplacements;

	public enum Orientation	{
		Horizontal,
		Vertical,
		Diagonal,
		AntiDiagonal;
	}

	public abstract int getTaille();
	public abstract int getResistance();
	public abstract int getMunitions();
	public abstract int getProfondeur();
	public abstract Position[] getEmplacements();
	public abstract Orientation getOrientation();
	public abstract int getDegats();
	public abstract Munition getMunitionCourante();
	public abstract Position getTete();

	public abstract void setMunition(int nb);
	public abstract void setResistance(int nb);
	public abstract void setTete(Position pos);
	public abstract void setOrientation(Orientation o);
	public abstract void setEmplacements(int index, Position pos);
	public abstract void setAvancee(int index, Orientation o);
	public abstract void setReculee(int index, Orientation o);

	public abstract void initMunitions();

	public void fillBoat()	{	// position de depart
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
		int z = 1; //si a la surface
		if (this instanceof SousMarin)	{
			double zAleatoire =  Math.random() * Globals.getProfondeurChampMax() + 1;
			z = (int) zAleatoire;
		}
		this.setTete(new Position((x-1),(y-1),(z-1)));
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
			if (!this.getEmplacements()[i].isInside())	{
				inside = false;
			}
		}
		return inside;
	}

	public boolean touchesA(Bateau[] bateaux)	{
		boolean isHit = false;
		for (int i = 0; i < bateaux.length; i++) {
			if (this != bateaux[i])	{ //pas de equals ici car on compare les adresses
				if (!bateaux[i].estCoule())	{
					if (this.touches(bateaux[i]))	{
						isHit = true;
					}
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
					isHit = true;
				}
			}
		}
		return (isHit);
	}

	public void avancer()	{
		for (int i = 0; i < this.getEmplacements().length; i++) {
			this.setReculee(i, this.getOrientation());
		}
	}

	public void reculer()	{
		for (int i = 0; i < this.getEmplacements().length; i++) {
			this.setAvancee(i, this.getOrientation());
		}
	}

	public int tirer(ChampBataille cb)	{

		Position pos = this.getEmplacements()[0]; // recupere la tête du bateau
		int id = 0;
		int j = pos.getY();
		int i = pos.getX();
		int z = pos.getZ();
		int[][][] t = cb.getT();

		if (this instanceof Croiseur)	{
			Globals.appendMessage(" lâche une mine");
			Globals.appendMessage(" d'une puissance de "+this.getMunitionCourante().getPuissance()+", d'une profondeur de "+this.getMunitionCourante().getProfondeur());
			z = 0;
			while (z < this.getMunitionCourante().getProfondeur() && id == 0) {
				z++;
				id = t[i][j][z];
			}
		}

		else	{
			Globals.appendMessage(" tire avec une puissance de "+this.getMunitionCourante().getPuissance()+", d'une profondeur de "+this.getMunitionCourante().getProfondeur()+", a une distance de "+ this.getMunitionCourante().getPortee() +" dans la direction");
			Scanner sc = new Scanner(System.in);
			int choix = 0;
			if (Globals.getTourDuJoueur())	{
				System.out.println("Dans quelle direction ?\n1. Nord\n2. Nord-Est\n3. Est\n4. Sud-Est\n5. Sud\n6. Sud-Ouest\n7. Ouest\n8. Nord-Ouest");
				choix = sc.nextInt();
			}
			else {
				double choixAleatoire =  Math.random() * 8 + 1;
				choix = (int) choixAleatoire;
			}
			int choixProfondeur = 0;

			if (this instanceof SousMarin)	{
				while (choixProfondeur < 1 || choixProfondeur > 2)	{
					if (Globals.getTourDuJoueur())	{
						if (this.getProfondeur() > 0)	{
							System.out.println("1. Vers le haut ?");
						}
						if (this.getProfondeur() < Globals.getProfondeurChampMax()-1)	{
							System.out.println("2. Vers le bas ?");
						}
						choixProfondeur = sc.nextInt();
					}
					else {
						double choixPAleatoire =  Math.random() * 2 + 1;
						choixProfondeur = (int) choixPAleatoire;
					}
				}
			}


			int limite = Globals.getLongueurChampMax()-1;
			int max = this.getMunitionCourante().getPortee();
			int imax = i + max;
			if (imax > limite) {imax = limite;}
			int jmax = j + max;
			if (jmax > limite) {jmax = limite;}
			int imin = i - max;
			if (imin < 0) {imin = 0;}
			int jmin = j - max;
			if (jmin < 0) {jmin = 0;}

			switch (choix) {
				case 1:
				Globals.appendMessage(" Nord");
				while (i > imin && id == 0) { //avancee N
					i--;
					id = t[i][j][z];
				}
				break;

				case 2:
				Globals.appendMessage(" Nord-Est");
				while (i > imin && j < jmax && id == 0) { //avancee NE
					i--;
					j++;
					id = t[i][j][z];
				}
				break;

				case 3:
				Globals.appendMessage(" Est");
				while (j < jmax && id == 0) { //avancee E
					j++;
					id = t[i][j][z];
				}
				break;

				case 4:
				Globals.appendMessage(" Sud-Est");
				while (i < imax && j < jmax && id == 0) { //avancee SE
					i++;
					j++;
					id = t[i][j][z];
				}
				break;

				case 5:
				Globals.appendMessage(" Sud");
				while (i < imax && id == 0) { //avancee S
					i++;
					id = t[i][j][z];
				}
				break;

				case 6:
				Globals.appendMessage(" Sud-Ouest");
				while (i < imax && j > jmin && id == 0) { //avancee SO
					i++;
					j--;
					id = t[i][j][z];
				}
				break;

				case 7:
				Globals.appendMessage(" Ouest");
				while (j > jmin) { //avancee O
					j--;
					id = t[i][j][z];
					if (id!=0)	{
						break;
					}
				}
				break;

				case 8:
				Globals.appendMessage(" Nord-Ouest");
				while (i > imin && j > jmin && id == 0) { //avancee NO
					i--;
					j--;
					id = t[i][j][z];
				}
				break;
			}
			if (this instanceof SousMarin && choixProfondeur != 0) {
				if (id == 0)	{
					if (choixProfondeur == 1)	{
						Globals.appendMessage(" vers le haut");
						int zmax =  z-this.getMunitionCourante().getProfondeur();
						while ((z > zmax && z > 0) && id == 0) {
							z--;
							id = t[i][j][z];
						}
					}
					if (choixProfondeur == 2)	{
						Globals.appendMessage(" vers le bas");
						while ((z < this.getMunitionCourante().getProfondeur() && z < Globals.getProfondeurChampMax()-1) && id == 0) {
							z++;
							id = t[i][j][z];
						}
					}
				}
			}
		}

		switch (id)  {
			case 0:
			Globals.appendMessage(" mais ne touche aucun bateau");
			break;
			case 1:
			Globals.appendMessage(", F1 est touche");
			break;
			case 2:
			Globals.appendMessage(", C1 est touche");
			break;
			case 3:
			Globals.appendMessage(", S1 est touche");
			break;
			case 4:
			Globals.appendMessage(", F2 est touche");
			break;
			case 5:
			Globals.appendMessage(", C2 est touche");
			break;
			case 6:
			Globals.appendMessage(", S2 est touche");
			break;
		}
		Globals.appendMessage(" aux coordonnees ("+i+"; "+j+"; "+z+")");
		return id;
	}

	public void refreshResistance(int dmg)	{
		this.setResistance(this.getResistance() - dmg);
	}

	public void useMunition()	{
		this.setMunition(this.getMunitions() - 1);
	}

	public boolean estCoule()	{
		boolean coule = false;
		if (this.getResistance() <= 0)	{
			coule = true;
		}
		return coule;
	}

	public boolean aEncoreMunitions()	{
		boolean munRestante = true;
		if (this.getMunitions() <= 0)	{
			munRestante = false;
		}
		return munRestante;
	}

	public String toString()	{
		String s = "";
		if (Globals.getTourDuJoueur())	{
			s+="Allies : ";
		}
		else	{
			s+="Ennemis : ";
		}
		if (this instanceof SousMarin)	{
			s+="Le sous-marin";
		}
		if (this instanceof Fregate)	{
			s+="La fregate";
		}
		if (this instanceof Croiseur)	{
			s+="Le croiseur";
		}
		s+=" en position "+this.getTete();
		return s;
	}
}
