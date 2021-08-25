package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
impEventCard ort io.qameta.allure.Severity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.TalkCard;
import pages.TalkPage;
import pages.TalksLibraryPage;

import java.time.LocalDate;

public class EpamTest {
    private static final Logger LOGGER = LogManager.getLogger(EpamTest.class);


    @Test
    @Feature("Просмотр предстоящих мероприятий")
    @DisplayName("Просмотр предстоящих мероприятий")
    void viewUpcomingEvents() {
        EventPage eventPage = openEventPage();
        Integer eventCardsCount = eventPage.getEventCardsCount();
        LOGGER.info("Текущие кол-во карточек с событиями: {}", eventCardsCount);
        Integer currentCounterValue = eventPage.getTabCounter();
        LOGGER.info("Текущий показатель счетчика вкладки 'Upcoming events': {}", currentCounterValue);
        Allure.step("На странице отображаются карточки предстоящих мероприятий. " +
                "Количество карточек равно счетчику на кнопке Upcoming Events");
        assertEquals(eventCardsCount, currentCounterValue,
                "Текущее значение счетчика не соответствует реальному кол-ву событий");
    }

    @Test
    @DisplayName("Просмотр карточек мероприятий")
    void viewEventCards() {
        EventPage eventPage = openEventTab("Upcoming events");
        EventCard rndEventCard = eventPage.getEventCard(1);
        Allure.step("В карточке указана информация о мероприятии: • язык • название мероприятия " +
                "• дата мероприятия • список спикеров");
        assertAll(
                () -> assertTrue(rndEventCard.isVisibleLanguage(), "не отображен язык события"),
                () -> assertTrue(rndEventCard.isVisibleEventName(), "не отображено имя события"),
                () -> assertTrue(rndEventCard.isVisibleDate(), "не отображена дата проведения события"),
                () -> assertTrue(rndEventCard.isVisibleSpeaker(), "не отображена информация о спикере")
        );
        Allure.description("Note: На момент написания теста, поле информации о регистрации отсутствовало в карточках события");
    }

    @Test
    @Feature("Валидация дат предстоящих мероприятий")
    @DisplayName("Валидация дат предстоящих мероприятий")
    void checkUpcomingEventDate() {
        EventPage eventPage = openEventTab("Upcoming events");
        EventCard eventCard = eventPage.getEventCard(1);
        String eventDate = eventCard.getDate();
        LocalDate start = EventDateParser.getFirstDateAtString(eventDate);
        LocalDate end = EventDateParser.getLastDateAtString(eventDate);
        LocalDate now = LocalDate.now();
        Allure.step("Даты проведения мероприятий больше или равны текущей дате " +
                "(или текущая дата находится в диапазоне дат проведения)");
        assertTrue(
                (start.isAfter(now) || start.isEqual(now)) && (end.isAfter(now) || start.isEqual(now)),
                "диапазон дат проведения мероприятий находиться в прошлом");
    }

    @Test
    @Feature("Просмотр прошедших мероприятий в Канаде")
    @DisplayName("Просмотр прошедших мероприятий в Канаде")
    @Description("Тест проверяет, корректность данных для карточек прошедших мероприятий в Канаде")
    void checkPastEventAfterLocationFilter() {
        EventPage eventPage = openEventTab("Past Events");
        Allure.step("Пользователь нажимает на Location в блоке фильтров и выбирает Canada в выпадающем списке");
        eventPage.goToFilter().locationFilter(SHOW).selectLocation("Canada");
        eventPage.waitCardLoader();
        Allure.step("На странице отображаются карточки прошедших мероприятий. " +
                "Количество карточек равно счетчику на кнопке Past Events. " +
                "Даты проведенных мероприятий меньше текущей даты.");
        assertAll(
                () -> assertTrue(eventPage.existEvents(), "на странице отсутствуют события"),
                () -> assertEquals(eventPage.getEventCardsCount(), eventPage.getTabCounter(),
                        "количество карточек не равно счетчику вкладки"),
                () -> assertTrue(
                        EventDateParser.getLastDateAtString(eventPage.getEventCard(eventPage.getTabCounter()).getDate())
                                .isBefore(LocalDate.now()),
                        "финальная дата события больше текущей даты")
        );
    }

    @Test
    @Feature("Фильтрация докладов по категориям")
    @DisplayName("Фильтрация докладов по категориям")
    void filterReportsByCategory() {
        TalksLibraryPage page = openTalksLibraryPage();
        String category = "Testing";
        String location = "Belarus";
        String language = "English";
        TalksLibraryPage results = searchTalksByParams(page, category, location, language);
        TalkPage rndTalkPage = results.getTalkCard(1).open();
        Allure.step("На странице отображаются карточки соответствующие правилам выбранных фильтров");
        assertAll(
                () -> assertTrue(rndTalkPage.getCategories().contains(category),
                        String.format("в списке категорий отсутствует '%s'", category)),
                () -> assertTrue(rndTalkPage.getLocation().contains(location),
                        "место проведения события не соответствует выбранному фильтру"),
                () -> assertTrue(rndTalkPage.getLanguage().equalsIgnoreCase(language),
                        "язык события не соответствует выбранному фильтру")
        );
    }

    @Test
    @Feature("Поиск докладов по ключевому слову")
    @DisplayName("Поиск докладов по ключевому слову 'QA'")
    @Description("Тест проверяет поиск докладов по ключевому слову QA")
    void searchTalkByWord() {
        TalksLibraryPage page = openTalksLibraryPage();
        String word = "QA";
        TalksLibraryPage results = searchTalksByName(page, word);
        TalkCard rndTalkCard = results.getTalkCard(1);
        Allure.step(String.format("На странице отображаются доклады, содержащие в названии ключевое слово поиска '%s'", word));
        assertTrue(rndTalkCard.getName().contains(word));

    }
}
