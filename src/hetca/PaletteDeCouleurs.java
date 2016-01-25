package hetca;

import java.awt.Color;



public final class PaletteDeCouleurs
{
  public static int Palette[] = new int[CAGird.MAX_CLO+1]; // color palette
  public static int Palette2[] = new int[21]; // color palette
  public static int GridColor[] = new int[2]; //normal, bold
  public static String PalName = "MJCell Standard";


  // ----------------------------------------------------------------
  // Make RGB value out of its 3 components
  private static int MakeRGB(int r, int g, int b)
  {
    return b + (g << 8) + (r << 16) + (0xff << 24);
  }
  // ----------------------------------------------------------------
  // Activate the given palette with the 'iSttCnt' states
  public static void  ActivatePalette(String palNam, int iSttCnt)
  {
    int i, j;
    GeneratePalette(Color.blue, Color.red, States.DECAY);
    GeneratePalette2(Color.black, Color.white);
    Palette[0] = Color.black.getRGB();
    Palette[States.DECAY] = Color.DARK_GRAY.getRGB();
    Palette[States.AUTODECAY] = Color.GRAY.getRGB();
 //   Palette[3] = Color.ORANGE.getRGB();
    GridColor[0] = 4194304 + (0xff << 24); // normal
    GridColor[1] = 6488833 + (0xff << 24); // bold
  }
  // ----------------------------------------------------------------
  // Generate the color palette
  public static void GeneratePalette2(Color c1, Color c2)
  { 
    int r, dr, r1, r2;
    int g, dg, g1, g2;
    int b, db, b1, b2;
    int i;
    r1 = c1.getRed();    r2 = c2.getRed();
    g1 = c1.getGreen();  g2 = c2.getGreen();
    b1 = c1.getBlue();   b2 = c2.getBlue();
    dr = (r2 - r1) / (20);
    dg = (g2 - g1) / (20);
    db = (b2 - b1) / (20);
    
    //Palette[0] = Color.black;
    for (i = 0; i < 21; i++)
    {
      if ((i == 20)) // the last color
      {
        Palette2[i] = MakeRGB(r2, g2, b2);
        //Palette[i] = new Color(r2, g2, b2);
      }
      else
      {
        Palette2[i] = MakeRGB(r1 + (i-1)*dr, g1 + (i-1)*dg, b1 + (i-1)*db);
        //Palette[i] = new Color(r1 + (i-1)*dr, g1 + (i-1)*dg, b1 + (i-1)*db);
      }
    }
  }
  public static void GeneratePalette(Color c1, Color c2, int iSttCnt)
  {
    int r, dr, r1, r2;
    int g, dg, g1, g2;
    int b, db, b1, b2;
    int i;
    r1 = c1.getRed();    r2 = c2.getRed();
    g1 = c1.getGreen();  g2 = c2.getGreen();
    b1 = c1.getBlue();   b2 = c2.getBlue();
    dr = (r2 - r1) / (iSttCnt - 1);
    dg = (g2 - g1) / (iSttCnt - 1);
    db = (b2 - b1) / (iSttCnt - 1);
    
    //Palette[0] = Color.black;
    for (i = 1; i < iSttCnt; i++)
    {
      if ((i == iSttCnt - 1) && (iSttCnt > 2)) // the last color
      {
        Palette[i] = MakeRGB(r2, g2, b2);
        //Palette[i] = new Color(r2, g2, b2);
      }
      else
      {
        Palette[i] = MakeRGB(r1 + (i-1)*dr, g1 + (i-1)*dg, b1 + (i-1)*db);
        //Palette[i] = new Color(r1 + (i-1)*dr, g1 + (i-1)*dg, b1 + (i-1)*db);
      }
    }
  }
  // ----------------------------------------------------------------
  // ----------------------------------------------------------------
  // ----------------------------------------------------------------

}
