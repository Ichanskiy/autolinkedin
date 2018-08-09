import java.time.LocalDate
import java.time.format.DateTimeFormatter

autolinkedin {
    user_first = "Anastasia";
    user_last = "Chernychenko";

    username = "arsenyuk.anastasia@gmail.com"
    password = "AAv8194739"
    user_caption = "${user_first} ${user_last}"

    search_contacts_location = "Ukra"
    search_contacts_fullLocationString = "Ukraine"
    search_contacts_position = "3D Artist"
    search_contacts_industries = []

    execution_limit = 200
    grabbing_limit = 100

    location = "UA";
    date_in_4_days = LocalDate.now().plusDays(4).format(DateTimeFormatter.ofPattern("MMM, d").withLocale(Locale.US));

    first_follow_up = "Äîáðûé äåíü, ${firstName},\n \n" +
            "Êîìïàíèÿ Kevuru Games ñåé÷àñ â ïîèñêå òàëàíòëèâûõ 3D õóäîæíèêîâ. Ïðåäëàãàåì èíòåðåñíûå ïðîåêòû, êîíêóðåíòíóþ çàðàáîòíóþ ïëàòó è ñîòðóäíè÷åñòâî ñ âñåìèðíî èçâåñòíûìè ñòóäèÿìè. Ïîäñêàæèòå, âàì áóäåò èíòåðåñíî ðàññìîòðåòü íàøå ïðåäëîæåíèå?\n" +
            "\n" +
            "Ñ óâàæåíèåì \n" +
            "Àíàñòàñèÿ\n";

    second_follow_up = "Hi ${firstName},\n" +
            "I found your profile in web and it would be good to stay in touch here on LinkedIn. " +
            "\n\n" +
            "Cheers, \n" +
            "${user_first}.\n";

}