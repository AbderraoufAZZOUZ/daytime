/**
* 03/13/2002 - 15:12:23
*
* DayTime - Affiche L'heure et la date du jour
* Copyright (C) 2002 Philippe BOUSQUET
* E-mail : Darken@tuxfamily.org
* Web : http://darken.tuxfamily.org
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
import java.applet.*;
import java.awt.*;
import java.util.*;
import java.text.*;
import java.lang.*;

public class DayTime extends java.applet.Applet implements Runnable
{
        private Thread runner;
        Font font;               // Fonte d'affichage 
        int fontSize;            // Taille des caractères
        Color backColor;         // Couleur de fond
        Color horlogeColor;      // Couleur de l'horloge
        Color fontColor;         // Couleur de fonte
        Color aiguilleColor;     // Couleur des aiguilles
        int rayon;               // Rayon de l'horloge
        String heure;
        String date;
        int margex;
        int margey;
        // double buffering
        private Graphics off;
        private Image offImg;

        /** Démarrage de l'applet */
        public void start()
        {
                if (runner == null)
                {
                        runner = new Thread(this);
                        runner.start();
                }
        }
        
        /** Initialisation de l'applet */
        public void init()
        {
            // image for double bffering
            offImg = createImage(size().width,size().height);
            // create a Graphics object from the image
            off = offImg.getGraphics();
            // Valeurs par défaut     
            fontSize = 9;
            backColor = Color.white;
            fontColor = Color.black;
            aiguilleColor = Color.blue;
            horlogeColor = Color.red;
            rayon = 60;
            // Récupération des paramètres
            String sizeS=getParameter("size");
            String bgcolorS=getParameter("bgcolor");
            String agcolorS=getParameter("agcolor");
            String ftcolorS=getParameter("ftcolor");
            String hlcolorS=getParameter("hlcolor");
            String rayonS=getParameter("rayon");
            // Interpretation des paramètres
            if (sizeS!=null) fontSize=Integer.parseInt(sizeS);
            font = new Font("Serif", Font.PLAIN, fontSize);
            if (decodeColor(bgcolorS)!=null) backColor = decodeColor(bgcolorS);
            if (decodeColor(agcolorS)!=null) aiguilleColor = decodeColor(agcolorS);
            if (decodeColor(ftcolorS)!=null) fontColor = decodeColor(ftcolorS);
            if (decodeColor(hlcolorS)!=null) horlogeColor = decodeColor(hlcolorS);
            if (rayonS!=null) rayon = Integer.parseInt(rayonS);
            // Debut
            setBackground(backColor);
            setFont(font);
        }
        
        /** Execution de l'applet */
        public void run()
        {
                Graphics g = getGraphics();
                while (runner != null)
                {
                        paint(off);
                        affiche_applet(g);
			try {Thread.currentThread().sleep(10); }
			catch (InterruptedException e) {}
                }
        }

/*        public void run()
        {
		Thread me = Thread.currentThread();
		while (runner == me) 
		{
			repaint();
			try {Thread.currentThread().sleep(400); }
			catch (InterruptedException e) {}
		}
        }
*/        
        /** Arret de l'applet */
        public void stop()
        {
                runner = null;
        }
        
        /** Destruction de l'applet */
        public void destroy()
        {
        }
        
        /** Affichage du contour de l'horloge */
        public void affiche_cercle(int r, Color c, Graphics g)
        {
            int i;
            int x,y;
            g.setColor(c);
            for (i=0;i<629;i++)
            {
              x=(int) (Math.cos(((float)i/100))*r+r+margex);      
              y=(int) (Math.sin(((float)i/100))*r+r+margey);      
              g.drawLine(x,y,x,y);
            }  
        }

        /** Affichage des points représentant les heures */
        public void affiche_heures(int r, Color c, Graphics g)
        {
            int h;
            int hx,hy;
            g.setColor(c);
            for (h=0;h<12;h++)
            {
               hx = (int) (Math.cos((h * 3.14 / 6)-3.14/2)*(r-3)+r+margex);
               hy = (int) (Math.sin((h * 3.14 / 6)-3.14/2)*(r-3)+r+margey);
               g.drawLine(hx,hy,hx,hy);
            }
        }
        
        /** Affichage des aiguilles */
        public void affiche_aiguille(int r,int x, int y, Color c, Graphics g)
        {
            g.setColor(c);
            g.drawLine(r+margex,r+margey,x,y);
        }
        
        /** Affichage de l'interface */
        public void paint(Graphics g)
        {
            int x,y;
            int h,m,s;
            int hx,mx,sx;
            int hy,my,sy;
            String chaine;
            
            Dimension d = getSize();
            margex=(int) ((d.width - (rayon * 2))/2);
            margey=(int) ((d.height - (rayon * 2))/2);            
            setSize(d);
            off.setColor(backColor);
            off.fillRect(0,0,size().width,size().height);
            Date dateCourante;
            dateCourante = new Date();
            date =(new SimpleDateFormat("dd/MM",Locale.getDefault())).format(dateCourante);
            heure =(new SimpleDateFormat("hh:mm",Locale.getDefault())).format(dateCourante);

            affiche_cercle(rayon,horlogeColor,g);
            affiche_heures(rayon,fontColor,g);
            // afficher la date
            FontMetrics fm = g.getFontMetrics();
	    x = (int) (rayon + margex - (fm.stringWidth(date))/2);            
            y = (int) (rayon/2 + margey + (g.getFont().getSize()));
            g.setColor(fontColor);
            g.drawString(date,x,y);
            // afficher l'heure
	    x = (int) (rayon + margex - (fm.stringWidth(heure))/2);            
            y = (int) (rayon + rayon/2 + margey - (g.getFont().getSize())/2);
            g.setColor(fontColor);
            g.drawString(heure,x,y);
            // afficher l'aiguille des heures
            h = Integer.parseInt((new SimpleDateFormat("hh",Locale.getDefault())).format(dateCourante));
            hx = (int) (Math.cos((h * 3.14 / 6)-3.14/2)*(rayon/2)+rayon+margex);
            hy = (int) (Math.sin((h * 3.14 / 6)-3.14/2)*(rayon/2)+rayon+margey);
            affiche_aiguille(rayon,hx,hy,aiguilleColor,g);
            // afficher l'aiguille des minutes
            m = Integer.parseInt((new SimpleDateFormat("mm",Locale.getDefault())).format(dateCourante));
            mx = (int) (Math.cos((m * 3.14 / 30)-3.14/2)*(rayon*9/10)+rayon+margex);
            my = (int) (Math.sin((m * 3.14 / 30)-3.14/2)*(rayon*9/10)+rayon+margey);
            affiche_aiguille(rayon,mx,my,aiguilleColor,g);
            // afficher l'aiguille des secondes
            s = Integer.parseInt((new SimpleDateFormat("ss",Locale.getDefault())).format(dateCourante));
            sx = (int) (Math.cos((s * 3.14 / 30)-3.14/2)*(rayon*9/10)+rayon+margex);
            sy = (int) (Math.sin((s * 3.14 / 30)-3.14/2)*(rayon*9/10)+rayon+margey);
            affiche_aiguille(rayon,sx,sy,fontColor,g);
        }
        
        public void affiche_applet(Graphics g)
        {
            g.drawImage(offImg, 0, 0, this);
        }

        /** Informations sur l'Applet */
	public String getAppletInfo() 
	{
		String retour;
		retour="Titre: DayTime\nVersion: 0.1.1\n"+
		"Description: Affiche la date et l'heure\n"+
		"Auteur: Philippe BOUSQUET\n"+
		"Copyright (c) 2002 - Philippe BOUSQUET\n"+
		"Cette Applet est sous licence GENERAL PUBLIC LICENSE\n";
		return retour;
	}
	
        /** Informations sur les parametres */
 	public String[][] getParameterInfo() 
	{
		String pinfo[][] = 
		{
			{"size", "integer", "Taille de la fonte","ex: 9"},
			{"bgcolor", "string", "Couleur de fond","ex: #FFFFFF"},
 			{"ftcolor", "string", "Couleur de la fonte","ex: #000000"},
 			{"agcolor", "string", "Couleur des aiguilles","ex: #0000FF"},
 			{"hlcolor", "string", "Couleur de l'horloge","ex: #FF0000"},
 			{"rayon", "integer", "Rayon de l'horloge","ex: 60"}
		};
		return pinfo;
	}

        /** Renvoyer la couleur donnée en paramètre */
        Color decodeColor(String s) 
	{
		int val = 0;
		try 
		{
	    		if (s.startsWith("0x")) { val = Integer.parseInt(s.substring(2), 16); } 
			else if (s.startsWith("#")) { val = Integer.parseInt(s.substring(1), 16); } 
			else if (s.startsWith("0") && s.length() > 1) {	val = Integer.parseInt(s.substring(1), 8); } 
			else { val = Integer.parseInt(s, 10); }
	    		return new Color(val);
		} 
		catch (NumberFormatException e) { return null; }
	}
}
