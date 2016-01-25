package hetca;

// Les import
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class Configuration {



    public static String getConfig(String fichier, String key) throws Exception {
	//On construit l'adresse du fichier
	String leFichier = System.getProperty("user.dir") + "/config/" + fichier;

	Properties config;
	String tmp;
	FileInputStream fis = new FileInputStream(leFichier);
	config = new Properties();
	config.load(fis);
	tmp = config.getProperty(key);

	if (tmp == null) {
	    // On leve une exeption
	    throw new Exception("La valeur correspondant à '" + key + "' n'existe pas dans le fichier '" + fichier + "'");
	}
	return tmp;

    }


    public static void setConfig(String fichier, String key, String valeur) throws Exception {
    	// La petite feinte : Il faur recharger entièrement le fichier 
	// et le réecrire.

	//On construit l'adresse du fichier
	String leFichier = System.getProperty("user.dir") + "/config/" + fichier;

	// On fait pointer notre Properties sur le fichier
	Properties config = new Properties();
	FileInputStream fis = new FileInputStream(leFichier);
	config.load(fis);
	FileOutputStream fos = new FileOutputStream(leFichier);
	config.setProperty(key, valeur);

	config.store(fos, "Dernière mise a jour :");

    }
}
