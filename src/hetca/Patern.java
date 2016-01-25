/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hetca;

// Referenced classes of package mjcell:

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

//            MJBoard

class Patern
{

    static Patern ParternList[] = new Patern[0x3a980];
    static Patern ParternListS[] = new Patern[0x3a980];
    static int totalShadow = 0;
    static int totalNorm = 0;
    static int totalNormNew = 0;
    private static int totalShadowNew = 0;
    private int id;
    private int totalpop;
    private int CycleCrea;
    private int CorX;
    private int CorY;

    static void cleanAll()
    {
        clean();
        cleanS();
    }

    static void initialize()
    {
        rezetS();
        rezet();
    }
    private boolean isnew = false;

    public Patern(int Newid)
    {
        id = -1;
        totalpop = 1;
        CycleCrea = 0;
        CorX = 0;
        CorY = 0;
        id = Newid;
    }

    public Patern()
    {
        id = -1;
        totalpop = 1;
        CycleCrea = 0;
        CorX = 0;
        CorY = 0;
        id = -1;
        totalpop = 0;
    }

    static void initiate()
    {
        for(int i = 0; i < ParternList.length; i++)
        {
            ParternList[i] = new Patern();
        }

    }

    static void rezet()
    {
        for(int i = 0; i < ParternList.length; i++)
        {
            ParternList[i].id = -1;
            ParternList[i].totalpop = 0;
        }

        totalNorm = 0;
        totalNormNew = 0;
    }

    static void clean()
    {
        for(int i = 0; i < ParternList.length && ParternList[i].id != -1; i++)
        {
            if(ParternList[i].totalpop == 0)
            {
                ParternList[i].id = -1;
            } else
            {
                ParternList[i].totalpop = 0;
                ParternList[i].isnew = false;
            }
        }

        totalNorm = 0;
        totalNormNew = 0;
    }

    static void initiateS()
    {
        for(int i = 0; i < ParternListS.length; i++)
        {
            ParternListS[i] = new Patern();
        }

    }

    static void rezetS()
    {
        for(int i = 0; i < ParternListS.length; i++)
        {
            ParternListS[i].id = -1;
            ParternListS[i].totalpop = 0;
        }

        totalShadow = 0;
        totalShadowNew = 0;
    }

    static void cleanS()
    {
        for(int i = 0; i < ParternListS.length && ParternListS[i].id != -1; i++)
        {
            if(ParternListS[i].totalpop == 0)
            {
                ParternListS[i].id = -1;
            } else
            {
                ParternListS[i].totalpop = 0;
            }
        }

        totalShadow = 0;
        totalShadowNew = 0;
    }

    static void TryToAdd(int Newid, int X, int Y)
    {
        int i;
        for(i = 0; i < ParternList.length && ParternList[i].id != Newid; i++) { }
        if(i == ParternList.length)
        {
            for(i = 0; i < ParternList.length && ParternList[i].id != -1; i++) { }
        }
        if(i == ParternList.length)
        {
            System.out.print("Full list !!!");
        }
        if(ParternList[i].id == -1)
        {
            ParternList[i].id = Newid;
            ParternList[i].totalpop = 1;
            totalNorm++;
            totalNormNew++;
            ParternList[i].CycleCrea = CAGird.Generation;
            ParternList[i].CorX = X;
            ParternList[i].CorY = Y;
            ParternList[i].isnew = true;
        } else
        {
            if(ParternList[i].totalpop == 0)
            {
                totalNorm++;
            }
            ParternList[i].totalpop++;
        }
    }

    static void TryToAddS(int Newid, int X, int Y)
    {
        int i;
        for(i = 0; i < ParternListS.length && ParternListS[i].id != Newid; i++) { }
        if(i == ParternListS.length)
        {
            for(i = 0; i < ParternListS.length && ParternListS[i].id != -1; i++) { }
        }
        if(i == ParternListS.length)
        {
            System.out.print("Full listS !!!");
        }
        if(ParternListS[i].id == -1)
        {
            ParternListS[i].id = Newid;
            ParternListS[i].totalpop = 1;
            totalShadow++;
            totalShadowNew++;
            ParternListS[i].CycleCrea = CAGird.Generation;
            ParternListS[i].CorX = X;
            ParternListS[i].CorY = Y;
            ParternList[i].isnew = true;
        } else
        {
            if(ParternListS[i].totalpop == 0)
            {
                totalShadow++;
            }
            ParternListS[i].totalpop++;
        }
    }

    static void DisplayPatern(String path)
        throws IOException
    {
        String returnval = "";
        int activity = 0;
        int NewActiv = 0;
        FileWriter lu = new FileWriter((new StringBuilder()).append(path).append(".txt").toString());
        BufferedWriter out = new BufferedWriter(lu);
        out.write((new StringBuilder()).append("Total number of patern : ").append(totalNorm).append("\n").toString());
        for(int key = 0; key < ParternList.length; key++)
        {
            if(ParternList[key].totalpop > 0)
            {
                if(ParternList[key].isNew()){
                    NewActiv ++;
                }
                activity += (CAGird.Generation - ParternList[key].CycleCrea);
                out.write((new StringBuilder()).append("ID : ").append(ParternList[key].id).append(", Cycle ").append(ParternList[key].CycleCrea).append(", Frequence : ").append(ParternList[key].totalpop).append(", Coordinate : ").append(ParternList[key].CorX).append("x").append(ParternList[key].CorY).toString()
                                        +", Activity : "+(CAGird.Generation - ParternList[key].CycleCrea));
                out.newLine();
            }
        }
        
        out.write("Diversity : "+totalNorm+", Bedau Mean cumulative evolutionary activity : "+((double)activity)/((double)totalNorm)+", Bedau new evolutionary activity : "+((double)NewActiv)/((double)totalNorm));
        
        out.close();
    }


    private boolean isNew() {
        boolean ret = false;
        if(this.CycleCrea>CAGird.Generation+150){
            ret = true;
        }
        return ret;
    }

}
