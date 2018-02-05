package org.cccrunching.parser.lalouviere;

import org.cccrunching.data.Meeting;
import org.cccrunching.data.Person;
import org.cccrunching.parser.MeetingParser;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LaLouvMeetingParserTest {

    @Test
    public void testMeetingTitle(){
        String title = "CONSEIL COMMUNAL DU JEUDI 27 NOVEMBRE 2017";
        LaLouvMeetingParser parser = new LaLouvMeetingParser();
        Meeting meeting = parser.createMeeting(title);
        assertEquals("CONSEIL COMMUNAL DU JEUDI 27 NOVEMBRE 2017", meeting.getTitle());
        assertTrue(meeting.getMeetingDate().isPresent());
        assertEquals(LocalDate.of(2017,11,27), meeting.getMeetingDate().get());
    }


    @Test
    public void testMeetingTitleEmptyLines(){
        String title = "\n\nCONSEIL COMMUNAL DU JEUDI 27 NOVEMBRE 2017\n\n";
        LaLouvMeetingParser parser = new LaLouvMeetingParser();
        Meeting meeting = parser.createMeeting(title);
        assertEquals("CONSEIL COMMUNAL DU JEUDI 27 NOVEMBRE 2017", meeting.getTitle());
        assertTrue(meeting.getMeetingDate().isPresent());
        assertEquals(LocalDate.of(2017,11,27), meeting.getMeetingDate().get());
    }

    @Test
    public void testMeetingSomeStuffBeforeAndAfter(){
        String title = "\nSome Stuff before\nCONSEIL COMMUNAL DU MERCREDI 8 MARS 2018\nblablabla\n";
        LaLouvMeetingParser parser = new LaLouvMeetingParser();
        Meeting meeting = parser.createMeeting(title);
        assertEquals("CONSEIL COMMUNAL DU MERCREDI 8 MARS 2018", meeting.getTitle());
        assertTrue(meeting.getMeetingDate().isPresent());
        assertEquals(LocalDate.of(2018,3,8), meeting.getMeetingDate().get());
    }

    @Test
    public void testTrimming(){
        String title = "\nSome Stuff before\n \t  CONSEIL COMMUNAL DU MERCREDI 8 MARS 2018       \nblablabla\n";
        LaLouvMeetingParser parser = new LaLouvMeetingParser();
        Meeting meeting = parser.createMeeting(title);
        assertEquals("CONSEIL COMMUNAL DU MERCREDI 8 MARS 2018", meeting.getTitle());
        assertTrue(meeting.getMeetingDate().isPresent());
        assertEquals(LocalDate.of(2018,3,8), meeting.getMeetingDate().get());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoTitleFound(){
        String title = "\nSome Stuff before\nblablabla\n";
        LaLouvMeetingParser parser = new LaLouvMeetingParser();
        parser.createMeeting(title);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWrongMonth(){
        String title = "\nSome Stuff before\n \t  CONSEIL COMMUNAL DU MERCREDI 8 VENDEMAIRE 2018       \nblablabla\n";
        LaLouvMeetingParser parser = new LaLouvMeetingParser();
        Meeting meeting = parser.createMeeting(title);
    }


    @Test
    public void attendeeExtractTest(){
        String text = "Sont présents : M.J.GOBERT,Mme A.DE LA PROET\n" +
                "MM.M.DESCHAMPS et R.MINELLI\n" +
                "Mmes T.LAMINE,K.DELAVIGNE et P.RUNELLE, M.M.MONSIEUR, Majordome en Chef\n" +
                "ORDRE DU JOUR";
        LaLouvMeetingParser parser = new LaLouvMeetingParser();
        List<Person> p =  parser.extractAttendees(text);
        assertEquals(Arrays.asList( new Person("J.GOBERT"),
                                    new Person("A.DE LA PROET"),
                                    new Person("M.DESCHAMPS"),
                                    new Person("R.MINELLI"),
                                    new Person("T.LAMINE"),
                                    new Person("K.DELAVIGNE"),
                                    new Person("P.RUNELLE"),
                                    new Person("M.MONSIEUR")), p);
    }

    @Test
    public void parseTest(){
        String fullText = "CONSEIL COMMUNAL DU LUNDI 27 NOVEMBRE 2017\n" +
                "\n" +
                "Sont présents : M.J.GOBERT, Bourgmestre-Président\n" +
                "Mme D.STAQUET, M.J.GODIN, Mme F.GHIOT, M.J.CHRISTIAENS,\n" +
                "MM.M.DI MATTIA, A.GAVA, L.WIMLOT, Echevins\n" +
                "Mme C.BURGEON, Présidente du CPAS, M.J.C.WARGNIE,\n" +
                "Mme A.SABBATINI, M.O.DESTREBECQ, Mme O.ZRIHEN, \n" +
                "MM.G.MAGGIORDOMO, F.ROMEO,\n" +
                "Mmes T.ROTOLO, I.VAN STEEN, A.DUPONT, MM.A.BUSCEMI, \n" +
                "A.FAGBEMI, M.VAN HOOLAND,\n" +
                "M.P.WATERLOT, Mme F.RMILI, M.C.LICATA, Mme M.ROLAND, \n" +
                "MM.A.HERMANT, A.CERNERO,\n" +
                "G.CARDARELLI, E.PRIVITERA, A.AYCIK, M.BURY, Mme B.KESSE,\n" +
                "M.D.CREMER, Mmes C.DRUGMAND,\n" +
                "C.BOULANGIER, MM.C.RUSSO, L.RESINELLI, J.LEFRANCQ, \n" +
                "H.SERBES et Mme N.NANNI, Conseillers communaux\n" +
                "M.R.ANKAERT, Directeur Général\n" +
                "En présence de M.E. MAILLET, Chef de Corps, en ce qui concerne les \n" +
                "points « Police »\n" +
                "\n" +
                "ORDRE DU JOUR" +
                "\n" +
                "Séance publique\n" +
                "\n" +
                "1.- Approbation du procès-verbal du Conseil communal du lundi 23 octobre 2017\n" +
                "\n" +
                "2.- Conseil communal - Remplacement de Monsieur Christophe DELPLANCQ, déchu de son \n" +
                "mandat originaire de conseiller communal - Installation du remplaçant - Prestation de serment - \n" +
                "Reconvocation\n" +
                "\n" +
                "3.- Décision de principe - Travaux d’aménagement des sanitaires du Conservatoire de musique \n" +
                "situé Place communale, 26 à La Louvière a)Choix du mode de passation du marché b)Approbation \n" +
                "du Cahier spécial des charges c)Approbation du mode de financement\n" +
                "\n" +
                "4.- Décision de principe - Département Infrastructure - Marché de fourniture relatif à \n" +
                "l'acquisition d'un chariot élévateur a)Choix du mode de passation du marché b)Approbation du \n" +
                "Cahier spécial des charges c)Approbation du mode de financement " +
                "La séance est ouverte à 19 heures 30\n" +
                "\n" +
                "Avant-séance\n" +
                "\n" +
                "M.Gobert   : Est-ce que je peux inviter les conseillers à prendre place ?\n" +
                "Nous allons commencer nos travaux en vous demandant de bien vouloir excuser l'absence de \n" +
                "Monsieur Lefrancq, l'arrivée tardive de Madame Zrihen et de Monsieur Destrebecq. Est-ce qu'il y a \n" +
                "d'autres demandes d'excuses ? Non ?\n" +
                " \n" +
                "Vous demander aussi de bien vouloir accepter 4 points qui sont des points relatifs à des assemblées \n" +
                "générales d'intercommunales. On peut les accepter ? Merci.\n" +
                " \n" +
                "\n" +
                "ORDRE DU JOUR\n" +
                "\n" +
                "Séance publique\n" +
                "\n" +
                "1.- Approbation du procès-verbal du Conseil communal du lundi 23 octobre 2017\n" +
                "\n" +
                "M.Gobert   : Nous allons commencer le Conseil par l'approbation du PV de notre séance du 23 \n" +
                "octobre.\n" +
                "On peut l'approuver ?\n" +
                " \n" +
                "M.Maggiordomo   : Monsieur le Bourgmestre, à ce propos, on reçoit chaque fois des points \n" +
                "supplémentaires après les commissions et c'est un peu embêtant. Ici, il y a 5 points supplémentaires \n" +
                "qu'on n'a pas eu le temps de... Il serait quand même intéressant de les avoir en commission.\n" +
                " \n" +
                "M.Gobert   : Ceux-ci sont en fait des points qui nous viennent des intercommunales et nous avons \n" +
                "des délais à respecter.\n" +
                "M.Maggiordomo   : Indépendamment de ceux-là, Monsieur le Bourgmestre.\n" +
                " \n" +
                "M.Gobert   : Oui, vous parlez de l'ordre du jour complémentaire ? Ils sont dans l'ordre du jour \n" +
                "complémentaire en fait, ce ne sont pas des points d'urgence comme aujourd'hui.\n" +
                "\n" +
                "2.- Conseil communal - Remplacement de Monsieur Christophe DELPLANCQ, déchu de son \n" +
                "mandat originaire de conseiller communal - Installation du remplaçant - Prestation de serment - \n" +
                "Reconvocation\n" +
                "\n" +
                "M.Gobert   : Nous allons passer au point 2. Vous le savez, depuis quelque temps déjà, il y a un siège \n" +
                "vacant au sein de notre Conseil. Ici, pour la seconde fois et donc la dernière, nous avons invité \n" +
                "Monsieur Michel Vanholland qui est censé pouvoir siéger comme conseiller communal. Est-ce qu'il \n" +
                "est dans la salle ?\n" +
                "\n" +
                "\n" +
                "\n" +
                " \n" +
                "Nous prenons acte de l'absence de Monsieur Michel Vanholland.\n" +
                " \n" +
                "Le Conseil,\n" +
                " \n" +
                "Vu,  d'une  part,  l'arrêté  du  Gouvernement  Wallon  du  22  avril  2004  portant  codification  de  la\n" +
                "législation  relative  aux  pouvoirs  locaux  et  d'autre  part,  le  décret  du  27  mai  2004  portant\n" +
                "confirmation dudit arrêté;\n" +
                " \n" +
                "Vu l'article 117 de la nouvelle Loi Communale;\n" +
                " \n" +
                "Vu l'article L 1122-30 du Code de Démocratie Locale et de la Décentralisation;\n" +
                " \n" +
                "Vu la délibération du Conseil communal prise en sa séance du 03 juin 2013;\n" +
                " \n" +
                "Vu la délibération du Conseil communal prise en sa séance du 09 septembre 2013;\n" +
                " \n" +
                "Vu la délibération du Conseil communal prise en sa séance du 25 avril 2016;\n" +
                " \n" +
                "Vu la délibération du Conseil communal prise en sa séance du 30 mai 2016;\n" +
                " \n" +
                "Vu la délibération du Conseil communal prise en sa séance du 19 septembre 2016;\n" +
                " \n" +
                "Vu la délibération du Conseil communal prise en sa séance du 24 octobre 2016;\n" +
                " \n" +
                "Vu la délibération du Conseil communal prise en sa séance du 28 novembre 2016;\n" +
                " \n" +
                "Vu la délibération du Conseil communal prise en sa séance du 19 décembre 2016;\n" +
                " \n" +
                "Vu la délibération du Conseil communal prise en sa séance du 30 janvier 2017;\n" +
                " \n" +
                "Vu la délibération du Conseil communal prise en sa séance du 20 février 2017;\n" +
                " \n" +
                "Vu la délibération du Conseil communal prise en sa séance du 20 mars 2017;\n" +
                " \n" +
                "Vu la délibération du Conseil communal prise en sa séance du 02 mai 2017;\n" +
                " \n" +
                "Vu la délibération du Conseil communal prise en sa séance du 25 septembre 2017;\n" +
                " \n" +
                "Considérant que Madame Magali LEJEUNE, en sa qualité de première suppléante de la liste FNW a\n" +
                "renoncé à son mandat de conseillère communale;\n" +
                " \n" +
                "Considérant que Monsieur Christophe DELPLANCQ installé après prestation de serment, en qualité\n" +
                "de conseiller communal indépendant, en remplacement de Monsieur Lucien DUVAL, a été déchu\n" +
                "de  son  mandat  originaire  de  conseiller  communal  ainsi  que  de  ses  mandats  dérivés  par  le\n" +
                "Gouvernement wallon;\n" +
                "Considérant  que  conformément  à  l'article  L1126-2  du Code  de  la  Démocratie  Locale  et  de  la\n" +
                "Décentralisation, Madame Mélanie DE SMET, en qualité de 3ème suppléante de la liste FNW a été\n" +
                "considérée  comme démissionnaire  en  raison de  ses  abstentions  sans  motifs  légitimes  de  prêter\n" +
                "serment, et ce, après avoir reçu deux convocations consécutives pour remplir cette formalité au CC\n" +
                "du 25 avril 2016 et ensuite au CC du 30 mai 2016;\n" +
                " \n" +
                "\n" +
                "\n" +
                "\n" +
                "Considérant  que  conformément  à  l'article  L1126-2  du Code  de  la  Démocratie  Locale  et  de  la\n" +
                "Décentralisation,  Monsieur Guy DARDENNE, en qualité de 4ème suppléant de la liste FNW a\n" +
                "également été considéré comme démissionnaire en raison de ses abstentions sans motifs légitimes\n" +
                "de  prêter  serment,  et  ce,  après  avoir  reçu  deux  convocations  consécutives  pour  remplir  cette\n" +
                "formalité au CC du 19 septembre 2016 et ensuite au CC du 24 octobre 2016;\n" +
                " \n" +
                "Considérant que Madame Jeannine LOYAERTS, en sa qualité de 5 ème suppléante de la liste FNW\n" +
                "a renoncé à son mandat de conseillère communale;\n" +
                " \n" +
                "Considérant  que  Madame  Françoise  RAMU,  6  ème  suppléante  de  la  liste  FNW,  a  également\n" +
                "renoncé à son mandat de conseillère communale;\n" +
                " \n" +
                "Considérant  que  conformément  à  l'article  L1126-2  du Code  de  la  Démocratie  Locale  et  de  la\n" +
                "Décentralisation, Madame Emilie DASCOTTE, 7ème suppléante de la liste FNW, a également été\n" +
                "considérée  comme démissionnaire  en  raison de  ses  abstentions  sans  motifs  légitimes  de  prêter\n" +
                "serment, et ce, après avoir reçu deux convocations consécutives pour remplir cette formalité au CC\n" +
                "du 30 janvier 2017 et ensuite au CC du 20 février 2017;\n" +
                " \n" +
                "Considérant  que  conformément  à  l'article  L1126-2  du Code  de  la  Démocratie  Locale  et  de  la\n" +
                "Décentralisation,  Monsieur Andy HARVENT, 8ème suppléant de la liste FNW, a également été\n" +
                "considéré  comme  démissionnaire  en  raison  de  ses  abstentions  sans  motifs  légitimes  de  prêter\n" +
                "serment, et ce, après avoir reçu deux convocations consécutives pour remplir cette formalité au CC\n" +
                "du 20 mars 2017 et ensuite au CC du 02 mai 2017;\n" +
                " \n" +
                "Considérant que Monsieur Michel VANHOLLAND, 9ème suppléant de la liste FNW, convoqué au \n" +
                "Conseil communal du 25 septembre 2017 afin de prêter serment en qualité de conseiller communal, \n" +
                "ne s'est pas présenté;\n" +
                " \n" +
                "Considérant qu'il appert que le courrier de convocation à la séance du Conseil du 23 octobre 2017 \n" +
                "n'est sans doute pas parvenu à Monsieur Michel VANHOLLAND, raison pour laquelle une re-\n" +
                "convocation de l'intéressé a paru nécessaire afin de s'assurer du respect du prescrit légal.\n" +
                " \n" +
                "Considérant que Monsieur Michel VANHOLLAND, convoqué une nouvelle fois au présent Conseil\n" +
                "communal, afin de prêter serment, en qualité de conseiller communal, ne s'est pas présenté.\n" +
                " \n" +
                "Considérant  que  conformément  à  l'article  L1126-2  du Code  de  la  Démocratie  Locale  et  de  la\n" +
                "Décentralisation, le mandataire qui, après avoir reçu deux convocations consécutives à l'effet de\n" +
                "prêter serment, s'abstient, sans motifs légitimes, de remplir cette formalité est considéré comme\n" +
                "démissionnaire.\n" +
                "\n" +
                "A l'unanimité,\n" +
                " \n" +
                "DECIDE :\n" +
                " \n" +
                "\n" +
                "Article 1: de prendre acte que Monsieur Michel VANHOLLAND, 9ème suppléant de la liste FNW, \n" +
                "a reçu deux convocations consécutives à l'effet de prêter serment au Conseil communal du 25 \n" +
                "septembre 2017 et ensuite au Conseil communal du 27 novembre 2017.\n" +
                " \n" +
                "Article 2: de prendre acte que Monsieur Michel VANHOLLAND s'est abstenu, sans motifs \n" +
                "légitimes, de remplir cette formalité.\n" +
                "Article 3: de prendre acte que Monsieur Michel VANHOLLAND est considéré comme \n" +
                "\n" +
                "\n" +
                "\n" +
                "démissionnaire, et ce, conformément à l'article L1126-2 du Code de la Démocratie Locale et de la \n" +
                "Décentralisation.\n" +
                " \n" +
                "Article 4: de convoquer le prochain suppléant de la liste FNW au prochain Conseil communal.\n" +
                "\n" +
                "3.- Décision de principe - Travaux d’aménagement des sanitaires du Conservatoire de musique \n" +
                "situé Place communale, 26 à La Louvière a)Choix du mode de passation du marché b)Approbation \n" +
                "du Cahier spécial des charges c)Approbation du mode de financement\n" +
                "\n" +
                "M.Gobert   : Les points suivants sont des décisions de principe, les points 3 à 6. Une demande \n" +
                "d'intervention pour l'un de ces points ? Monsieur Cremer, pour quel point ?\n" +
                " \n" +
                "M.Cremer   : Le point 3.\n" +
                " \n" +
                "M.Gobert   : Vous avez la parole.\n" +
                " \n" +
                "M.Cremer   : Merci, Monsieur le Bourgmestre. On va enfin refaire les toilettes du Conservatoire de \n" +
                "La Louvière ici sur la place communale; c'est très chouette. Les enfants et les professeurs seront très\n" +
                "contents.\n" +
                " \n" +
                "Je fais un petit historique quand même parce que j'aime bien rappeler les choses.\n" +
                "Question écrite de ma personne le 25 septembre 2014, il y a donc trois ans, je signale que les \n" +
                "toilettes du Conservatoire sont vétustes, qu'elles présentent de multiples problèmes. On me répond \n" +
                "le 20 novembre, et vous me répondez en substance : « Il n'y a rien à signaler, tout va bien, rien n'a \n" +
                "jamais posé de problème. »\n" +
                " \n" +
                "J'ai continué à soulever le problème et aujourd'hui, trois ans après, c'est devenu vétuste d'un coup, \n" +
                "chouette ! On va enfin pouvoir faire quelque chose. Je me dis qu'à force de taper sur le clou, \n" +
                "finalement, on finit par être entendu.\n" +
                "Peut-être que nous serions plus entendus si nous étions plus près !\n" +
                " \n" +
                "J'ai oublié de poser la question en commission. Est-ce que dans les toilettes, il y aura une toilette \n" +
                "« personne à mobilité réduite » qui sera construite ? Nous en avons discuté en commission, il y a \n" +
                "aussi un problème d'ascenseur au Conservatoire de La Louvière puisqu'il n'y a pas d'ascenseur et \n" +
                "donc, les personnes PMR ne peuvent pas faire des arts de la parole, de la musique à La Louvière.\n" +
                "Je sais que vous avez réfléchi au problème de l'ascenseur et que vous continuerez à réfléchir, ça, \n" +
                "c'est chouette, mais on pourrait déjà anticiper le futur et prévoir déjà maintenant des toilettes pour \n" +
                "personnes à mobilité réduite, et ça, je ne sais pas si on a prévu.\n" +
                " \n" +
                "M.Gobert   : Difficile de vous répondre.\n" +
                " \n" +
                "M.Cremer   : J'ai lu le cahier des charges et ça n'était pas mentionné. Je ne demande pas une réponse\n" +
                "ce soir, Monsieur le Bourgmestre.\n" +
                " \n" +
                "M.Gobert   : Cela va être difficile. C'est dans les étages ou au rez-de-chaussée ? Parce que si vous \n" +
                "dites qu'il n'y a pas d'accessibilité pour les personnes à mobilité réduite, donc par définition, il faut \n" +
                "monter. Si les personnes ne savent pas monter parce qu'il n'y a pas d'ascenseur, vous voulez des \n" +
                "toilettes pour les PMR ?\n" +
                " \n" +
                "M.Cremer   : Monsieur le Bourgmestre, vous me décevez ce soir ! Je vous dis : « Gouverner, c'est \n" +
                "prévoir. »\n" +
                " \n" +
                "\n" +
                "\n" +
                "\n" +
                "M.Gobert   : Vous voulez qu'on anticipe pour quand on va mettre l'ascenseur dans trois ans, en fait ?\n" +
                " \n" +
                "M.Cremer   : Je vous dis : « Gouverner, c'est prévoir. », donc anticipons déjà le fait que vous allez \n" +
                "un jour mettre des ascenseurs et réalisons déjà les toilettes pour personnes à mobilité réduite \n" +
                "maintenant, ce n'est pas pour le surcoût que ça demande.\n" +
                "M.Gobert   : Ce n'est pas forcément une question de surcoût.\n" +
                " \n" +
                "M.Cremer   : Je pense qu'on peut déjà prévoir l'avenir plutôt que de se trouver un jour coincé. \n" +
                "J'espère qu'un jour, il y aura un ascenseur dans cet établissement. Je propose qu'on regarde dans le \n" +
                "cahier des charges, en dehors du Conseil, et qu'on y pense.\n" +
                " \n" +
                "M.Gobert   : Mais si vous dites que non !\n" +
                " \n" +
                "M.Cremer   : Je pense mais je peux m'être trompé, Monsieur le Bourgmestre.\n" +
                " \n" +
                "M.Gobert   : Non, ça m'étonnerait de vous.\n" +
                " \n" +
                "M.Cremer   : Vous êtes gentil ce soir !\n" +
                " \n" +
                "M.Gobert   : Si vous me dites que non, ça veut dire que non ?\n" +
                " \n" +
                "M.Cremer   : On peut encore y penser.\n" +
                " \n" +
                "M.Gobert   : Pour les points 3 à 6, d'autres demandes d'intervention ? On peut les approuver ? Merci.\n" +
                " \n" +
                "Le Conseil,\n" +
                " \n" +
                "Vu le Code de la démocratie locale et de la décentralisation et ses modifications ultérieures ;\n" +
                " \n" +
                "Vu la loi du 29 juillet 1991 relative à la motivation formelle des actes administratifs ;\n" +
                " \n" +
                "Vu la loi du 17 juin 2016 relative aux marchés publics, notamment l'article 42 §1, 1°, a);\n" +
                " \n" +
                "Vu l'Arrêté Royal du 18 avril 2017 relatif à la passation des marchés publics dans les secteurs \n" +
                "classiques ;\n" +
                " \n" +
                "Vu l'Arrêté Royal du 14 janvier 2013 établissant les règles générales d'exécution des marchés \n" +
                "publics et des concessions de travaux publics, tel que modifié par l'Arrêté Royal du 22 juin 2017 ;\n" +
                " \n" +
                "Vu l'avis financier de légalité de la Directrice financière ;\n" +
                " \n" +
                "Considérant qu’il convient de passer un marché de travaux d’aménagement des sanitaires du \n" +
                "Conservatoire de musique situé Place communale, 26 à La Louvière ; \n" +
                " \n" +
                "Considérant que ces travaux sont nécessaires car les sanitaires sont vétustes et les occupants \n" +
                "rencontrent régulièrement des problèmes avec les évacuations d’eau ;\n" +
                " \n" +
                "Considérant que l’estimation du montant du marché s’élève à 93.955,00 € HTVA soit 99.592,30 € \n" +
                "TVAC ;\n" +
                " \n" +
                "Considérant que le mode de passation proposé est la procédure négociée sans publication préalable, \n" +
                "sur pied de l'article 42, §1, 1° a) de la loi du 17 juin 2016 ;\n" +
                "\n" +
                "\n" +
                "\n" +
                "Considérant qu’un crédit de 120.000,00 € est prévu à l'article 734/72402-60 du budget \n" +
                "extraordinaire 2017 et la dépense sera couverte par un emprunt/un fonds de réserve ;\n" +
                " \n" +
                "Considérant qu’au vu de son montant estimé, le présent marché n’est pas soumis aux règles de \n" +
                "publicité européenne.\n" +
                "Vu l'avis du Directeur financier repris ci-dessous ainsi qu'en annexe :\n" +
                "« 1. Projet de délibération au Conseil communal référencé : «Décision de principe - BE - T - AFL-\n" +
                "B5/PL/ID/2017V036 – Travaux d’aménagement des sanitaires du Conservatoire de musique situé\n" +
                "Place communale, 26 à La Louvière a)Choix du mode de passation du marché b)Approbation du\n" +
                "Cahier spécial des charges c)Approbation du mode de financement.»\n" +
                " \n" +
                "2. Contrôle effectué dans le cadre de l'article L1124-40 §1, 3° du CDLD et dont l'étendue porte sur\n" +
                "le projet de décision précité et son annexe: le cahier des charges (clauses administratives).\n" +
                " \n" +
                "3. De cette analyse, il ressort que l'avis est favorable.\n" +
                " \n" +
                "Toutefois, il est à noter que :\n" +
                " \n" +
                "Le poste « sommes à justifier » est prévu en QP. La formule du forfait semble plus appropriée pour\n" +
                "encadrer ce concept qui, bien que communément admis dans la pratique, n'a pas de fondement\n" +
                "légal.»\n" +
                " \n" +
                "A l'unanimité,\n" +
                " \n" +
                "DECIDE :\n" +
                " \n" +
                "Article 1   : de lancer le marché public : travaux d’aménagement des sanitaires du Conservatoire de \n" +
                "musique situé Place communale, 26 à La Louvière ;\n" +
                "\n" +
                " \n" +
                "Article 2   : de choisir la procédure négociée sans publication préalable comme mode de passation du\n" +
                "marché ;\n" +
                " \n" +
                "Article 3   : d’approuver le cahier spécial des charges tels que repris en annexe de la présente \n" +
                "délibération ;\n" +
                " \n" +
                "Article 4   : d’acter que le mode de financement est : l’emprunt/le fond de réserve et que la dépense \n" +
                "est prévue à l'article 734/72402-60 du budget extraordinaire 2017.\n" +
                " La séance est levée à 21:15\n" +
                "\n" +
                "Par le Conseil,\n" +
                "\n" +
                "Le Directeur Général, Le Bourgmestre,\n" +
                "\n" +
                "R.ANKAERT J.GOBERT";

            MeetingParser parser = new LaLouvMeetingParser();
            Meeting meeting = parser.parse(fullText);
            assertEquals("CONSEIL COMMUNAL DU LUNDI 27 NOVEMBRE 2017", meeting.getTitle());
            assertEquals(4,meeting.getItems().size());
    }

}
