import easyIO.*;
import java.util.*;

class Varprogram {
    public static void main (String[] args) {

	/**
	 * Program: Varprogram
	 * Note: Jeg ser naa at flere ting som gjores dobbelt opp burde vart i metoder.
	 *@author benjamat
	 */

	System.out.println("*** VARDATA-O-RAMA ***");

	/**
	 *Konstruerer MetInst om to argumenter foreligger, hvis ikke skriver den ut hva som maa gjores.
	 *@param args[];
	 **/
	if (args.length >= 2) {
	    MetInst metInst = new MetInst(args[0],args[1]);//args[0],args[1]
	    metInst.hovedmeny();
	} else {
	    System.out.println("Bruk: >java Oblig4 <fil med Stasjonsdata> < fil med Observasjonsdata>");
	}

	System.out.println("*** VARDATA-O-RAMA ***");

    }
}

class MetInst {
    /**
     *Hovedklasse.
     *Inneholder ordrelokke til programmetshovedfunksjoner og innlesningsmetoder + regionkonstruktoren.
     */

    String args[];
    String stasjonTXT;
    String klimaTXT;

    /**  MetInst-konstruktor som ogsaa lager regioner, slik at de er initialiserte til innlesningene starter.*/
    MetInst(String stasjon, String klima) {//String stasjon2, String klima2 //String stasjon, String klima
	regionKreator("OSTLANDET");//Tror denne skal startes forst.
	regionKreator("AGDER");
	regionKreator("NORD-NORGE");
	regionKreator("VESTLANDET");
	regionKreator("TRONDELAG");
	regionKreator("NORGE");
	
	stasjonTXT = stasjon;
	klimaTXT = klima;
	//this.stasjon2 = args[0];
	//	this.klima2 = args[1];
	//Filleser fil
	filLeser(stasjonTXT);
	filLeser2(klimaTXT);
	//klimadata2012.txt stasjoner_norge.txt
    }

    HashMap<String, Object> stasjonsListe = new HashMap<String, Object>();  
    HashMap<String, Object> regionListe = new HashMap<String, Object>();

    /** Hovedmenyordrelokke som programmet returnerer til etter ferdig jobb. */
    void hovedmeny() {
	In tast = new In();

	int ordre = 0;
	while (ordre != 5) {
	    System.out.println("Mulige valg: ");
	    System.out.println("Valg 1: Finn antall uvarsdager.");
	    System.out.println("Valg 2: Sammenlign vind ved kyststasjoner mot resten.");
	    System.out.println("Valg 3: Sammenlign ostlandet mot nordnorge.");
	    System.out.println("Valg 4: Finn kaldeste verstasjonmaaned.");
	    System.out.println("Valg 5: Avslutt.");
	    System.out.print("Ditt valg: ");
	    ordre = tast.inInt ();
	    switch (ordre) {
	    case 1: finnAntallUversDager();break;
	    case 2: sammenlignVindVedKyststasjonerMotResten();break;
	    case 3: sammenlignOstlandetMotNordnorge();break;
	    case 4: finnKaldesteVerstasjonMaaned();break;
	    case 5: avslutt();break;

	    default: hovedmeny();
	    }
	}

    }

    void finnAntallUversDager() {
	/** Metode for aa finne antall uvarsdager for en stasjons spesifikke maaned. 
	 * Note: Metoden takler mangel paa data, men ikke feilinnstastet stasjon. Da blir det nullpoint.
	 */

	In tast = new In();

	System.out.println("*** Finn antall uvarsdager ***");
	System.out.print("Velg stasjon. Oppgi stasjonsnummer: ");
	String stasjonsNummer = tast.inWord();
	System.out.print("Velg maaned: ");
	int maaned = tast.inInt();

	Stasjon stasjon = (Stasjon) stasjonsListe.get(stasjonsNummer);

	int uvar = stasjon.maanedArray[maaned].uvarsdagRegner();

	if (stasjon.maanedArray[maaned].uvarsdagRegner() == 6000) {
	    System.out.println("Det finnes ikke data for denne maaneden."); 
	} else {

	    System.out.println("Stasjonsnavn: " + stasjon.stasjonsnavn);
	    System.out.println("Maaned: " + maaned);
	    System.out.println("Antall uvarsdager: " + uvar);

	}

    }

    void sammenlignVindVedKyststasjonerMotResten() {
	/** Metode for aa sammenligne vind ved kysten med innlandsstasjoners vind. */
	System.out.println("*** Sammenlign vind ved kyststasjoner mot resten ***");

	double totalVindKysten = 0;
	double maanederKysten = 0;
	double totalVindInnland = 0;
	double maanederInnland = 0;

	for (String key: stasjonsListe.keySet()) {

	    Stasjon stasjon = (Stasjon) stasjonsListe.get(key);
	    if (stasjon.meterOverHavet <= 60) {
		
		for (int i = 0; i<13; i++) {
		    //stasjon.maaned[i].vindRegner();
		    if (stasjon.maanedArray[i].vindRegner()!=6000) {
			totalVindKysten += stasjon.maanedArray[i].vindRegner();
			maanederKysten++;
		    } 

		}

	    } else {

		for (int i = 0; i<13; i++) {
		    stasjon.maanedArray[i].vindRegner();
		    if (stasjon.maanedArray[i].vindRegner()!=6000) {
			totalVindInnland += stasjon.maanedArray[i].vindRegner();
			maanederInnland++;
		    }

		}

	    }
	}

	double kystGjennomsnitt = totalVindKysten / maanederKysten;
	double innlandGjennomsnitt = totalVindInnland / maanederInnland;

	System.out.println("Blaaser det mer ved kysten enn andre steder?");
	System.out.println("Gjennomsnittlig vindhastighet per dag ved kysten: " + kystGjennomsnitt + " km/t.");
	System.out.println("Gjennomsnittlig vindhastighet per dag innland: " + innlandGjennomsnitt + " km/t.");

	if (kystGjennomsnitt > innlandGjennomsnitt) {
	    System.out.println("Det blaaser faktisk mer ved kysten!");
	} else {
	    System.out.println("Paastanden er feilaktig. Det blaaser ikke mer ved kysten.");
	}

    }

    void sammenlignOstlandetMotNordnorge() {
	/** Metode for aa sammenligne regionen ostlandet mot regionen nordnorge paa temperatur og nedbor. */

	In tast = new In();
	Out forbindelseUt = new Out("Resultat.txt", true);

	System.out.println("*** Sammenlign ostlandet mot nordnorge ***");
	System.out.print("Velg maaned: ");
	int maaned = tast.inInt();

	double snittRegnLagerNN = 0;
	double snittTempLagerNN = 0;//Temperatur

	double snittRegnLagerOL = 0;
	double snittTempLagerOL = 0;//Temperatur

	int stasjonTellerOL = 0;
	int stasjonTellerOLRegn = 0;//Viktig med egen teller. Om det finnes stasjonsmaaneder uten nedborsdata.

	int stasjonTellerNN = 0;
	int stasjonTellerNNRegn = 0;

	if (snittTempLagerOL==0) {//Disse to burde vart i maaneder.
	    Region region = (Region) regionListe.get("OSTLANDET");
	    for (String key: region.stasjonsListeRegion.keySet()) {

		Stasjon stasjon = (Stasjon) region.stasjonsListeRegion.get(key);

		if (stasjon.maanedArray[maaned].tempRegner()!=6000) {
		    stasjonTellerOL++;
		    snittTempLagerOL += stasjon.maanedArray[maaned].tempRegner();
		} 

		if (stasjon.maanedArray[maaned].regnRegner()!=6000) {
		    stasjonTellerOLRegn++;
		    snittRegnLagerOL += stasjon.maanedArray[maaned].regnRegner();
		} 

	    }

	}

	if (snittTempLagerNN==0) {
	    Region region = (Region) regionListe.get("NORD-NORGE");
	    for (String key: region.stasjonsListeRegion.keySet()) {

		Stasjon stasjon = (Stasjon) region.stasjonsListeRegion.get(key);

		if (stasjon.maanedArray[maaned].tempRegner()!=6000) {
		    stasjonTellerNN++;
		    snittTempLagerNN += stasjon.maanedArray[maaned].tempRegner();
		}

		if (stasjon.maanedArray[maaned].regnRegner()!=6000) {
		    stasjonTellerNNRegn++;
		    snittRegnLagerNN += stasjon.maanedArray[maaned].regnRegner();
		} 

	    }

	}

        double snittTempNN = snittTempLagerNN / stasjonTellerNN;
	double snittRegnNN = snittRegnLagerNN / stasjonTellerNNRegn;

	double snittTempOL = snittTempLagerOL / stasjonTellerOL;
	double snittRegnOL = snittRegnLagerOL / stasjonTellerOLRegn;

	if (Double.isNaN(snittRegnOL)) {//Hvis en er NaN, anses alle som NaN.
	    System.out.println("Det finnes ikke data for denne maaneden.");
	} else {
	    
	    System.out.println("Snitttemp NN: " + snittTempNN);
	    System.out.println("Snittregn NN: " + snittRegnNN);
	    System.out.println("Snitttemp OL: " + snittTempOL);
	    System.out.println("Snittregn OL: " + snittRegnOL);

	    //Utskrift til filen Resultat.txt
	    forbindelseUt.outln("Maaned: " + maaned);
	    forbindelseUt.outln("Gjennomsnittstemperatur i Nord-Norge: " + snittTempNN);
	    forbindelseUt.outln("Gjennomsnittstemperatur pa Ostlandet: " + snittTempOL);
	    forbindelseUt.outln("Gjennomsnittsnedbor i Nord-Norge    : " + snittRegnNN);
	    forbindelseUt.outln("Gjennomsnittsnedbor pa Ostlandet    : " + snittRegnOL);
	    forbindelseUt.close();

	    if(snittTempOL > snittTempNN) {

		if (snittRegnOL < snittRegnNN) {
		    System.out.println("Det regner mindre og er varmere paa Ostlandet!");
		} else {
		    System.out.println("Det er varmere paa Ostlandet, men det regner mer enn i nordnorge.");
		}

	    } else {

		if (snittRegnNN < snittRegnOL) {
		    System.out.println("Det regner mindre og er varmere i Nordnorge!");
		} else {
		    System.out.println("Det er varmere i Nordnorge, men det regner mer.");
		}

	    }

	}		

    }

    void finnKaldesteVerstasjonMaaned() {
	/** Metode som finner den kaldeste stasjonen blandt alle stasjonene paa en maaned. */
	
	In tast = new In();

	double vinnerTemp = 100;//100 grader. Ammarite?
	String vinnerNavn = "";

	System.out.println("*** Finn kaldeste verstasjonmaaned ***");
	System.out.print("Velg maaned: ");
	int maaned = tast.inInt();

	for (String key: stasjonsListe.keySet()) {

	    Stasjon stasjon = (Stasjon) stasjonsListe.get(key);

	    if (stasjon.maanedArray[maaned].tempRegner() != 6000) {
		if (stasjon.maanedArray[maaned].tempRegner() < vinnerTemp) {
		    vinnerTemp = stasjon.maanedArray[maaned].tempRegner();
		    vinnerNavn = stasjon.stasjonsnavn;
		}
	    }

	}

	if (vinnerTemp == 100) {
	    System.out.println("Det finnes ikke data for denne maaneden.");
	} else {
	    System.out.println("Maaned: " + maaned);
	    System.out.println("Kaldeste stasjon: " + vinnerNavn);
	    System.out.println("Stasjonens snittemperatur denne maaneden: " + vinnerTemp);
	}

    }

    void avslutt() {
	/** Metode som avslutter programmet. */
	System.out.println("*** Avslutt ***");
	System.out.println("Takk for i dag!");
    }

    void filLeser(String stasjon2) {
	/** Metode som leser inn stasjonslistefilen. 
	 * @param stasjon2
	 */

	In forbindelse = new In(stasjon2);

	String toppsetning = forbindelse.readLine();
	
	while(!forbindelse.lastItem()) {

	    int stasjonsnummerLest = forbindelse.inInt();
	    String stasjonsnavnLest = forbindelse.inWord();
	    int meterOverHavetLest = forbindelse.inInt();
	    String kommuneLest = forbindelse.inWord();
	    String fylkeLest = forbindelse.inWord();
	    String regionLest = forbindelse.inWord();

	    Region region = (Region) regionListe.get(regionLest);
	    Stasjon stasjon = new Stasjon(stasjonsnummerLest, stasjonsnavnLest, meterOverHavetLest, kommuneLest, fylkeLest, regionLest);

	    stasjonsListe.put(stasjon.stasjonsnummer+"", stasjon);
	    stasjon.overSkriver();
	    
	    region.stasjonsListeRegion.put(stasjonsnavnLest, stasjon);

	}
	forbindelse.close();
    }

    void filLeser2(String klima) {
	/** Metode som leser inn klimadata.
	 * @param klima
	 */

	In forbindelse = new In(klima);

	for (int i = 0; i<9; i++) {
	    forbindelse.readLine();
	}

	while(!forbindelse.lastItem()) {
	    int stasjonsnummer = forbindelse.inInt();
	    int maaned = forbindelse.inInt();
	    int dag = forbindelse.inInt();
	    int time = forbindelse.inInt();
	    double lufttemperatur = forbindelse.inDouble();
	    double nedbor24Timer = forbindelse.inDouble();
	    double nedbor12Timer = forbindelse.inDouble();
	    double vindretning = forbindelse.inDouble();
	    double vindhastighet = forbindelse.inDouble();

	    int m = maaned;
	    int d = dag;
	    String nokkel = stasjonsnummer+"";

	    Stasjon stasjon = (Stasjon) stasjonsListe.get(nokkel);

	    stasjon.maanedArray[m].dagArray[d].putDataHer(stasjonsnummer, maaned, dag, lufttemperatur, nedbor24Timer, nedbor12Timer, vindretning, vindhastighet);
	    //Siden objektene allerede eksisterer, skriver jeg heller over verdiene. Tomme dager blir staaende med stasjonsnummer og resten som -999, slik at de lett kan kontrolleres som tomme senere.

	}
	forbindelse.close();

    }

    void regionKreator(String regionNavn) {
	/**
	 * Metode som lager nye regioner.
	 * @param regionNavn
	 */

	Region region = new Region(regionNavn);
	regionListe.put(regionNavn, region);
   
    }

}

/**
 *Klasse region
 *@param regionNavn Navnet til regionen. Brukes som nokkel i hashmap og til aa konstruere regioner.
 */
class Region{
   
    String regionNavn;

    HashMap<String, Object> stasjonsListeRegion = new HashMap<String, Object>();

    Region(String regionNavn){
	this.regionNavn = regionNavn;	
    }

}

/**
 *Klasse Stasjon
 *Stasjonsklassen hentes ved bruk av stasjonshashmap, eller region hashmap.
 *Stasjonskonstruktoren igangsettes hver gang en ny stasjon leses inn.
 *Naar det skjer, kjores stasjonens overskriver ogsaa, for aa hindre nullpoint-exception
 *Stasjoner har et objectarray av maaneder, som igjen har et objektarray av dager som inneholder dagens vaerinformasjon
 *Om dagen er tom(f.eks dag 0 i hver maaned) blir stasjonsnummeret til dagobjektet staaende som -999 som brukes for aa identifisere dagen som tom.
 *@author benjamat
 */
class Stasjon {
    
    /** Stasjonenes stasjonsnummer */
    int stasjonsnummer;
    /** Stasjonenes stasjonsnavn */
    String stasjonsnavn;
    /** Stasjonenes hoyde over havet (meter) */
    int meterOverHavet;
    /** Stasjonenes kommune */
    String kommune;
    /** Stasjonenes fylke */
    String fylke;
    /** Stasjonenes region */
    String region;

    /** Stasjonenes objektarray av maaneder. Hver maaned peker igjen videre paa sin maaned. 
     * Fra 0-13 slik at 1-12 kan brukes uten minus. De tomme maanedene fylles med tomme kontrollerbare maaneder.
     */
    Maaned[] maanedArray = new Maaned[13];

    /** Stasjonens konstruktor //Feilmelding? ...
     * @param stasjonsnummerLest int Stasjonsnummeret fra lesing.
     * @param stasjonsnavnLest String Stasjonsnavnet fra lesing.
     * @param meterOverHavetLest int MOH fra lesing.
     * @param kommuneLest String Kommunen til stasjonen fra lesing.
     * @param fylkeLest String Fylke til stasjonen fra lesing.
     * @param regionLest String Region til stasjonen fra lesing.
     */
    Stasjon(int stasjonsnummerLest, String stasjonsnavnLest, int meterOverHavetLest, String kommuneLest, String fylkeLest, String regionLest) {
  
	stasjonsnummer = stasjonsnummerLest;
	stasjonsnavn = stasjonsnavnLest;
	meterOverHavet = meterOverHavetLest;
	kommune = kommuneLest;
	fylke = fylkeLest;
	region = regionLest;
	
    }

    //Lager objekter, slik at jeg ikke faar nullpoint-exception senere.
    /** Metode overskriver
     * Overskriver maanedene paa forhand slik at jeg ikke faar nullpointexception, men tomme objekter som programmet sjekker om er tomme senere.
     */
    void overSkriver() {

	for (int i = 0; i<13; i++) {
	    maanedArray[i] = new Maaned();
	    for (int j = 0; j<32; j++) {

		int stasjonsnummer2 = -999;
		int maaned2 = -999;
		int dag2 = -999;
		double lufttemperatur2 = -999;
		double nedbor2 = -999;
		double vindretning2 = -999;
		double vindhastighet2 = -999;
		    
		maanedArray[i].dagArray[j] = new Dag(stasjonsnummer2, maaned2, dag2, lufttemperatur2, nedbor2, vindretning2, vindhastighet2);
		    		  
	    }
	}

    }

}

class Maaned{
    /** Klasse maaned
     * Hver maaned inneholder et objektarray av dager.
     * Innholder flere metoder for utregning av snitt og andre typer data.
     */
    /** Objektarray av dager. */
    Dag[] dagArray = new Dag[32];

    /** Metode som regner ut gjennomsnittnedbor for en maaned. */
    double regnRegner() {

	int dagTeller = 0;
	double totalNedbor = 0;

	for (int i = 0; i<32; i++) {

	    if (dagArray[i].nedbor<-500) {
	    } else {
		totalNedbor += dagArray[i].nedbor;
		dagTeller++;
	    }

	}
	
	if (dagTeller==0) {
	    return 6000;
	} else {
	    double nedborSnitt = totalNedbor / dagTeller;
	    return nedborSnitt;
	}

    }

    /** Metode som regner ut gjennomsnitttemp for en maaned. */
    double tempRegner() {

	int dagTeller = 0;
	double totalTemp = 0;

	for (int i = 0; i<32; i++) {

	    if (dagArray[i].lufttemperatur<-500) {//Gjor ingenting hvis kontrollen intreffer.
	    } else {
		double dagSnitt = dagArray[i].lufttemperatur / dagArray[i].lufttempTeller;
		totalTemp += dagSnitt;
		dagTeller++;
	    }

	}

	if (dagTeller==0) {
	    return 6000;
	} else {
	    double tempSnitt = totalTemp / dagTeller;
	    return tempSnitt;
	}

    }

    /** Metode som regner ut gjennomsnittvind for en maaned. */
    double vindRegner() {
	int dagTeller = 0;
	double vind = 0;

	for (int i = 0; i<32; i++) {

	    if (dagArray[i].vindhastighet>0) {
		dagTeller++;
		double dagensVindSnitt = dagArray[i].vindhastighet / dagArray[i].vindhastighetTeller;//Boolean-metode som sjekker om det er uvar den dagen eller ei.
		vind += dagensVindSnitt;
	    } 

	}

	if (dagTeller==0) {
	    return 6000;
	} else { 
	    double vindSnitt = vind / dagTeller;
	    return vindSnitt;
	}

    }

    /** Metode som regner ut antall uvarsdager for en maaned for en maaned. */
    int uvarsdagRegner() {

	int uvarsdager = 0;
	int tomMaaned = 0;

	for (int i = 0; i<32; i++) {
	    if (dagArray[i].erUvarsdag()) {//Boolean-metode som sjekker om det er uvar den dagen eller ei.
		uvarsdager++;
	    }
	    if (dagArray[i].stasjonsnummer==-999) {
		tomMaaned++;
	    }
	}

	if (tomMaaned == 32) {
	    return 6000;
	} else {
	    return uvarsdager;
	}

    }

}

class Dag{

    int stasjonsnummer;
    int maaned;
    int dag;
    double lufttemperatur;
    double nedbor;
    double vindretning;
    double vindhastighet;
    int nedbor24Teller = 0;
    int nedbor12Teller = 0;
    int vindhastighetTeller = 0;
    int lufttempTeller = 0;
    boolean uvarsDag;

    /** Boolean-metode hvor dagen selv sjekker om den er en nedborsdag eller ikke. */
    boolean erUvarsdag() {
	double dagensVindSnitt = vindhastighet / vindhastighetTeller;//Sjekker ikke snitt for den trenger det.
	double nedborVind = nedbor + dagensVindSnitt;
	return nedborVind>10.7;
    }

    /**Metode som mottar data fra datainnlesningen
     * Kontrollerer ogsaa og finner snitt der den kan.
     * Eksisterer for at programmet ikke skal faa en overflod av tomme objekter.
     */
    void putDataHer(int stasjonsnummer, int maaned, int dag, double lufttemperatur, double nedbor24Timer, double nedbor12Timer, double vindretning, double vindhastighet) {

	this.stasjonsnummer = stasjonsnummer;
	this.maaned = maaned;
	this.dag = dag;

	//Lufttemp
	if (this.lufttemperatur==-999) {
	    if (lufttemperatur > -100) {
		this.lufttemperatur = lufttemperatur;
		lufttempTeller++;
	    }
	} else {
	    if (lufttemperatur > -100) {
		this.lufttemperatur += lufttemperatur;
	        lufttempTeller++;
	    }
	}
	
	//Nedbor
	if (nedbor24Timer > -900) {
	    this.nedbor = nedbor24Timer;
	    nedbor24Teller++;
	}
		
	if (nedbor12Timer > -999) {
	    nedbor12Teller++;
	}
	if (nedbor12Teller==1) {
	    if (nedbor24Teller==0) {
		this.nedbor = nedbor12Timer;
		double nedbor12Total = this.nedbor + nedbor12Timer;
		this.nedbor = nedbor12Total;
	    }
	}
	if (nedbor12Teller==2) {
	    if (nedbor24Teller==0) {
		double nedbor12Total = this.nedbor / 2;
		this.nedbor = nedbor12Total + nedbor12Timer;
	    }
	}
	if (nedbor12Teller>=3) {
	    //Ingenting skjer. Har dataen jeg trenger.
	}

	//Vindretning
	this.vindretning = vindretning;
	//Brukes ikke av programmet. Derfor regner den ikke snitt.

	//Vindhastighet
	if (this.vindhastighet==-999) {
	    if (vindhastighet>-999) {
		this.vindhastighet = vindhastighet;
		vindhastighetTeller++;
	    }
	} else {
	    if (vindhastighet>-999) {
		this.vindhastighet = vindhastighet;
		vindhastighetTeller++;
	    }
	}

    }

    Dag(int stasjonsnummer, int maaned, int dag, double lufttemperatur, double nedbor, double vindretning, double vindhastighet) {
	this.stasjonsnummer = stasjonsnummer;
	this.maaned = maaned;
	this.dag = dag;
	this.lufttemperatur = lufttemperatur;
	this.nedbor = nedbor;
	this.vindretning = vindretning;
	this.vindhastighet = vindhastighet;
    }

}
