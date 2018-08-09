import java.time.LocalDate
import java.time.format.DateTimeFormatter

autolinkedin {
    user_first = "Sergey";
    user_last = "Kornitsky";
    username = "kornitsky@ukr.net"
    username = "abaranovskiy1985@gmail.com"
    password = "700bigbn"
    user_caption = "${user_first} ${user_last}"

    search_contacts_location = "Los Ang"
    search_contacts_fullLocationString = "Greater Los Angeles Area"
    search_contacts_position = "CTO"
    search_contacts_industries = ["games", "gamb", "soft", "program", "consult", "graphic", "techno"]

    execution_limit = 3
    grabbing_limit = 10

    location = "LA";
    date_in_4_days = LocalDate.now().plusDays(4).format(DateTimeFormatter.ofPattern("MMM, d").withLocale(Locale.US));

    first_follow_up = "Hi ${firstName},\n" +
            "How are you? \n" +
            "So, will be there a chance to catch up for the lunch or coffee in ${location} after ${date_in_4_days}? " +
            "\n\n" +
            "Cheers, \n" +
            "${user_first}.\n";

    second_follow_up = "Hi ${firstName},\n" +
            "I found your profile in web and it would be good to stay in touch here on LinkedIn. " +
            "\n\n" +
            "Cheers, \n" +
            "${user_first}.\n";

}