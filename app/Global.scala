import controllers.ProvidesCBForm
import models._
import models.Addon
import models.Feature
import models.FeatureHub
import models.Room
import models.RoomListing
import play.api.db.slick.Config.driver.simple._
import play.api._
import play.api.mvc._
import play.api.mvc.Request
import play.api.mvc.Results._
import play.api.http.Status
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple.Session
import play.api.mvc.WithFilters
import play.api.Play.current
import play.filters.gzip.GzipFilter
import play.api.http.HeaderNames
import scala.concurrent.Future
import views.html._


object Global extends WithFilters(new GzipFilter()) with TrailingSlash {

  import play.api.Play.current

  override def onRouteRequest(request: RequestHeader): Option[Handler] = {
    if (isCloudbees(request)) Some(redirectToNormal(request))
    else super.onRouteRequest(request)
  }

  private def isCloudbees(request: RequestHeader): Boolean = request.host.endsWith(".net")
  private def redirectToNormal(request: RequestHeader) = Action { Redirect("http://www.riga-royal.ru") }

  override def onStart(app: Application) {
    InitialData.insert()
    InitialData.getCurrency()
  }

  object InitialData {

    def getCurrency() {
      DB.withSession { implicit s:Session =>
        Currencys.getCurrencyFromServer
      }
    }

    def insert() {

      DB.withSession { implicit s: Session =>
          if(Rooms.count == 0) {
            /*  case class RoomListing (
                                       id: Option[Int] = None,
              name: String,
              photoUrl: String,
              photoAlt: String,
              iconUrl: String,
              ctatext: String
              )*/

            Seq(
              RoomListing(Option(1), "Волнующая романтика номера Paris", "images/rooms/paris.jpg", "Номер Париж в Royal Casino SPA and Hotel Resort", "images/rooms/paris-icon.png", "Узнайте больше о роскошном номере для утонченных личностей."),
              RoomListing(Option(2), "Ароматный восток в номере Dubai", "images/rooms/dubai.jpg", "Номер Дубай в Royal Casino SPA and Hotel Resort", "images/rooms/dubai-icon.png", "Узнайте больше о роскошном номере для утонченных личностей."),
              RoomListing(Option(3), "Венецианский стиль в номере Venice", "images/rooms/venecia.jpg", "Номер Венеция в Royal Casino SPA and Hotel Resort", "images/rooms/venice-icon.png", "Узнайте больше о роскошном номере для утонченных личностей."),
              RoomListing(Option(4), "Город вдохновения в номере Vienna", "images/rooms/viene.jpg", "Номер Вена в Royal Casino SPA and Hotel Resort", "images/rooms/viene-icon.png", "Узнайте больше о роскошном номере для утонченных личностей."),
              RoomListing(Option(5), "Приятные воспоминания в номере Moscow", "images/rooms/moscow.jpg", "Номер Москва в Royal Casino SPA and Hotel Resort", "images/rooms/moscow-icon.png", "Узнайте больше о роскошном номере для утонченных личностей."),
              RoomListing(Option(6), "Модный до мелочей New York", "images/rooms/newyork.jpg", "Номер Нью-Йорк в Royal Casino SPA and Hotel Resort", "images/rooms/newyork-icon.png", "Узнайте больше о роскошном номере для утонченных личностей."),
              RoomListing(Option(7), "В гостях у Карла VII в номере Versace", "images/rooms/versace.jpg", "Номер Версаче в Royal Casino SPA and Hotel Resort", "images/rooms/versace-icon.png", "Узнайте больше о роскошном номере для утонченных личностей."),
              RoomListing(Option(8), "Восхищение архитектурой в номере Barcelona", "images/rooms/barcelona.jpg", "Номер Барселона в Royal Casino SPA and Hotel Resort", "images/rooms/barcelona-icon.png", "Узнайте больше о роскошном номере для утонченных личностей."),
              RoomListing(Option(9), "Английский стиль в номере London", "images/rooms/london.jpg", "Номер Лондон в Royal Casino SPA and Hotel Resort", "images/rooms/london-icon.png", "Узнайте больше о роскошном номере для утонченных личностей."),
              RoomListing(Option(10), "Азарт в номере Las Vegas", "images/rooms/lasvegas.jpg", "Номер Лас Вегас в Royal Casino SPA and Hotel Resort", "images/rooms/vegas-icon.png", "Узнайте больше о роскошном номере для утонченных личностей."),
              RoomListing(Option(11), "Жажда роскоши в Monte Carlo", "images/rooms/montecarlo-listing.jpg", "Номер Монте Карло в Royal Casino SPA and Hotel Resort", "images/rooms/montecarlo-icon.png", "Узнайте больше о роскошном номере для утонченных личностей."),
              RoomListing(Option(12), "Сказки Андерсона в Амстердаме", "images/rooms/amsterdam-listing.jpg", "Номер Амстердам в Royal Casino SPA and Hotel Resort", "images/rooms/amsterdam-icon.png", "Узнайте больше о роскошном номере для утонченных личностей."),
              RoomListing(Option(13), "Госпожа удача в номере Рулетка", "images/rooms/roulette-listing.jpg", "Номер Рулетка в Royal Casino SPA and Hotel Resort", "images/rooms/roulette-icon.png", "Узнайте больше о роскошном номере для утонченных личностей."),
              RoomListing(Option(14), "Высокая ставка в номере Покер", "images/rooms/poker-listing.jpg", "Номер Покер в Royal Casino SPA and Hotel Resort", "images/rooms/poker-icon.png", "Узнайте больше о роскошном номере для утонченных личностей."),
              RoomListing(Option(15), "Скорость мысли в номере Блэкджэк", "images/rooms/blackjack-listing.jpg", "Номер Блэкджек в Royal Casino SPA and Hotel Resort", "images/rooms/blackjack-icon.png", "Узнайте больше о роскошном номере для утонченных личностей.")
            ).foreach(RoomListings.insert)

            /*case class Room (id: Option[Int] = None, name: String, photoUrl: String, text: String, price: Int, squaremeters: Int)*/

          Seq(
            Room(Option(1), "Paris", "images/rooms/paris-1.jpg", "Номер Париж в Royal Casino SPA and Hotel Resort",
              "Атмосфера волнующей романтики Парижа XIX века, которой наполнена каждая комната номера, подарит незабываемые воспоминания вам и вашим любимым.",
              700, Option(40), 136, Option(1)),

            Room(Option(2), "Dubai", "images/rooms/dubai-1.jpg", "Номер Дубай в Royal Casino SPA and Hotel Resort",
              "185 кв. метров роскоши и удовольствий – это номер DUBAI. В просторных комнатах вы сможете провести незабываемо любой праздник, будь то день рождения, годовщина свадьбы или мальчишник." +
              "<br/><br/>Эксклюзивный дизайн, комфортные функциональные зоны, позволяющие каждому проводить время, как он пожелает.",
              1500, Option(40), 185, Option(2)),

            Room(Option(3), "Venice", "images/rooms/venecia-1.jpg", "Номер Венеция в Royal Casino SPA and Hotel Resort",
              "Изысканное великолепие венецианского стиля, романтика и безудержная страсть, свойственная столице всех влюбленных Венеции, воплотились в этом номере. Пастельные приятные тона создают гармоничную атмосферу, в которой приятно отдыхать.",
              450, Option(15), 67, Option(3)),

            Room(Option(4), "Vienna", "images/rooms/viene-1.jpg", "Номер Вена в Royal Casino SPA and Hotel Resort",
              "Номер, созданный специально для влюбленных. Здесь продумано все – роскошная кровать, множество зеркал в изысканных рамах, стилизованная под старину мебель.<br/><br/>Декоративные элементы интерьера (комоды, расписные потолки) подчеркивают исторический стиль Вены. Приятные пастельные тона дарят ощущение гармонии и теплоты.",
              450, Option(25), 76, Option(4)),

            Room(Option(5), "Moscow", "images/rooms/moscow-1.jpg", "Номер Москва в Royal Casino SPA and Hotel Resort",
              "Вы попадете в неожиданную и так многим знакомую атмосферу огромного московского мегаполиса. В номере Москва все создано для самых изысканных постояльцев.",
              1500, None, 190, Option(5)),

            Room(Option(6), "New York", "images/rooms/newyork-1.jpg", "Номер Нью-Йорк в Royal Casino SPA and Hotel Resort",
              "Экстравагантный номер-люкс наделен своим особенным характером. В модном интерьере слились воедино черный и белый, свобода и благородство, сдержанность и шик.<br/><br/>Этот номер по праву можно назвать жемчужиной отеля. Модный дизайн продуман до мелочей. Каждая деталь, будь то статуэтка, светильник или мебель являются завершенным штрихом художественной красоты.",
              700, None, 135, Option(6)),

            Room(Option(7), "Versace", "images/rooms/versace-1.jpg", "Номер Версаче в Royal Casino SPA and Hotel Resort",
              "Этот номер особенный. Умеренная роскошь, без лишних броских деталей, но с обилием элементов из дерева, шелковых тканей и натурального камня – идиллия высокого стиля.<br/><br/>Каждая комната – произведение искусства. Каждое решение гармонично дополняет единый ансамбль. Просторные комнаты, изящество в классическом интерьере создают ощущение легкости и внутреннего комфорта.",
              1000, None, 135, Option(7)),

            Room(Option(8), "Barcelona", "images/rooms/barcelona-1.jpg", "Номер Барселона в Royal Casino SPA and Hotel Resort",
              "Неповторимость и очарование Барселоны, воплощенные в мягких художественных формах мебели, красочной мозаике, удивительных рельефах стен и обтекаемых зеркалах, дарят постояльцам номера новые ощущения.<br/><br/>Эстетическая красота, романтичность и утонченный стиль архитектора Гауди действительно стоят вашего восхищения.",
              550, None, 82, Option(8)),

            Room(Option(9), "London", "images/rooms/london-1.jpg", "Номер Лондон в Royal Casino SPA and Hotel Resort",
              "Традиционная красота и элегантность – три синонима английского стиля. Этот номер понравится людям, которые предпочитают классические ценности, лаконичность и сдержанность в выражении мыслей, идей и в интерьере.<br/><br/>В номере вы найдете кресла и столик, за которым хочется сесть и выпить хорошего чая или виски. Кожаные пуфы для большего удобства.",
              450, None, 58, Option(9)),

            Room(Option(10), "Las Vegas", "images/rooms/lasvegas-1.jpg", "Номер Лас Вегас в Royal Casino SPA and Hotel Resort",
              "Лас Вегас - город, который притягивает самых рискованных и азартных людей со всего света. В номере вы будете удивлены изысканным стилем, высоким уровнем декора.",
              270, None, 49, Option(10)),

            Room(Option(11), "Monte Carlo", "images/rooms/montecarlo-booking.jpg", "Номер Монте Карло в Royal Casino SPA and Hotel Resort",
              "Побалуйте себя неспешной княжеской жизнью, присущей этому миниатюрному городу возле лазурного моря!",
              270, None, 43, Option(11)),

            Room(Option(12), "Amsterdam", "images/rooms/amsterdam-booking.jpg", "Номер Амстердам в Royal Casino SPA and Hotel Resort",
              "Атмосфера королевского пафоса и неудержимой воли, независимости и демократичности перекочевала из столицы европейского государства прямиком в апартаменты, предложенные вам. ",
              270, None, 58, Option(12)),

            Room(Option(13), "Roulette", "images/rooms/roulette-booking.jpg", "Номер Рулетка в Royal Casino SPA and Hotel Resort",
              "Отправляя шарик по своему новому кругу, вы ставите на зеро? Удача подобной ставки может быть и не так велика, а возможность осуществить свои мечты и поселиться в головокружительно красивом номере «Roulette» — гораздо ближе.  ",
              270, None, 38, Option(13)),

            Room(Option(14), "Poker", "images/rooms/poker-booking.jpg", "Номер Покер в Royal Casino SPA and Hotel Resort",
              "Вас утомило играть в загадочность и прятать свое настоящее лицо за вынужденным «poker face»? Позвольте себе быть собой, устроившись в таком же захватывающем и увлекательном, как эта игра, номере.   ",
              270, None, 32, Option(14)),

            Room(Option(15), "Black Jack", "images/rooms/blackjack-booking.jpg", "Номер Блэкджек в Royal Casino SPA and Hotel Resort",
              "Простые стратегии и правила, молниеносность партии и скорость вашей остроумной мысли — все это вам пригодится в игре Блэкджек, хотя жизнь в данных апартаментах потребует от вас полнейшего раскрепощения и избалует отдыхом.    ",
              270, None, 32, Option(15))
          ).foreach(Rooms.insert)

          /*case class Feature (
                                   id: Option[Int] = None,
                                   iconUrl: String,
                                   text: String
                                   )*/

          Seq(
            Feature(Option(1), "images/options/beth.png", "Сауна в<br>номере"),
            Feature(Option(2), "images/options/bed.png", "2 кровати<br>King-Size"),
            Feature(Option(3), "images/options/bed.png", "1 кровать<br>King-Size"),
            Feature(Option(4), "images/options/snowflake.png", "Кондиционер"),
            Feature(Option(5), "images/options/tv.png", """ Телевизор 107" """),
            Feature(Option(6), "images/options/fitness.png", "Бесплатный фитнес"),
            Feature(Option(7), "images/options/clubbing.png", "Посещение ночных клубов")
          ).foreach(Features.insert)

            /*case class FeatureHub (
                                    id: Option[Int] = None
                                    roomsId: Option[Int] = None,
                                    featureId: Option[Int] = None,
                                    )*/

          Seq(
            /*1 Paris */
            FeatureHub(Option(1), Option(1), Option(1)),
            FeatureHub(Option(2), Option(1), Option(2)),
            FeatureHub(Option(3), Option(1), Option(4)),
            FeatureHub(Option(4), Option(1), Option(5)),
            FeatureHub(Option(5), Option(1), Option(6)),
            FeatureHub(Option(5), Option(1), Option(7)),

            /*2 Dubai */
            FeatureHub(Option(6), Option(2), Option(1)),
            FeatureHub(Option(7), Option(2), Option(2)),
            FeatureHub(Option(9), Option(2), Option(4)),
            FeatureHub(Option(10), Option(2), Option(5)),
            FeatureHub(Option(11), Option(2), Option(6)),
            FeatureHub(Option(12), Option(2), Option(7)),


            /*3 Venice */
            FeatureHub(Option(13), Option(3), Option(1)),
            FeatureHub(Option(14), Option(3), Option(3)),
            FeatureHub(Option(16), Option(3), Option(4)),
            FeatureHub(Option(17), Option(3), Option(5)),
            FeatureHub(Option(18), Option(3), Option(6)),
            FeatureHub(Option(19), Option(3), Option(7)),


            /*4 Viena */
            FeatureHub(Option(13), Option(4), Option(1)),
            FeatureHub(Option(14), Option(4), Option(3)),
            FeatureHub(Option(16), Option(4), Option(4)),
            FeatureHub(Option(17), Option(4), Option(5)),
            FeatureHub(Option(18), Option(4), Option(6)),
            FeatureHub(Option(19), Option(4), Option(7)),


            /*5 Moscow */
            FeatureHub(Option(21), Option(5), Option(1)),
            FeatureHub(Option(22), Option(5), Option(2)),
            FeatureHub(Option(23), Option(5), Option(4)),
            FeatureHub(Option(24), Option(5), Option(5)),
            FeatureHub(Option(25), Option(5), Option(6)),
            FeatureHub(Option(26), Option(5), Option(7)),


            /*6 New York */
            FeatureHub(Option(28), Option(6), Option(1)),
            FeatureHub(Option(29), Option(6), Option(2)),
            FeatureHub(Option(30), Option(6), Option(4)),
            FeatureHub(Option(31), Option(6), Option(5)),
            FeatureHub(Option(32), Option(6), Option(6)),
            FeatureHub(Option(33), Option(6), Option(7)),


            /*7 Versace */
            FeatureHub(Option(35), Option(7), Option(1)),
            FeatureHub(Option(36), Option(7), Option(2)),
            FeatureHub(Option(37), Option(7), Option(4)),
            FeatureHub(Option(38), Option(7), Option(5)),
            FeatureHub(Option(39), Option(7), Option(6)),
            FeatureHub(Option(40), Option(7), Option(7)),


            /*8 Barcelona */
            FeatureHub(Option(42), Option(8), Option(1)),
            FeatureHub(Option(43), Option(8), Option(2)),
            FeatureHub(Option(44), Option(8), Option(4)),
            FeatureHub(Option(45), Option(8), Option(5)),
            FeatureHub(Option(46), Option(8), Option(6)),
            FeatureHub(Option(47), Option(8), Option(7)),


            /*9 London */
            FeatureHub(Option(49), Option(9), Option(1)),
            FeatureHub(Option(50), Option(9), Option(2)),
            FeatureHub(Option(51), Option(9), Option(4)),
            FeatureHub(Option(52), Option(9), Option(5)),
            FeatureHub(Option(53), Option(9), Option(6)),
            FeatureHub(Option(54), Option(9), Option(7)),


            /*10 Las Vegas */
            FeatureHub(Option(56), Option(10), Option(1)),
            FeatureHub(Option(57), Option(10), Option(2)),
            FeatureHub(Option(58), Option(10), Option(4)),
            FeatureHub(Option(59), Option(10), Option(5)),
            FeatureHub(Option(60), Option(10), Option(6)),
            FeatureHub(Option(61), Option(10), Option(7)),

              /*11 Monte Carlo */
            FeatureHub(Option(62), Option(11), Option(1)),
            FeatureHub(Option(63), Option(11), Option(2)),
            FeatureHub(Option(64), Option(11), Option(4)),
            FeatureHub(Option(65), Option(11), Option(5)),
            FeatureHub(Option(66), Option(11), Option(6)),
            FeatureHub(Option(67), Option(11), Option(7)),

            /*12 Amsterdam */
            FeatureHub(Option(68), Option(12), Option(1)),
            FeatureHub(Option(69), Option(12), Option(3)),
            FeatureHub(Option(70), Option(12), Option(4)),
            FeatureHub(Option(71), Option(12), Option(5)),
            FeatureHub(Option(72), Option(12), Option(6)),
            FeatureHub(Option(73), Option(12), Option(7)),

            /*13 Roulette */
            FeatureHub(Option(74), Option(13), Option(1)),
            FeatureHub(Option(75), Option(13), Option(3)),
            FeatureHub(Option(76), Option(13), Option(4)),
            FeatureHub(Option(77), Option(13), Option(5)),
            FeatureHub(Option(78), Option(13), Option(6)),
            FeatureHub(Option(79), Option(13), Option(7)),

            /*14 Poker */
            FeatureHub(Option(80), Option(14), Option(1)),
            FeatureHub(Option(81), Option(14), Option(3)),
            FeatureHub(Option(82), Option(14), Option(4)),
            FeatureHub(Option(83), Option(14), Option(5)),
            FeatureHub(Option(84), Option(14), Option(6)),
            FeatureHub(Option(85), Option(14), Option(7)),

            /*15 Black Jack */
            FeatureHub(Option(86), Option(15), Option(1)),
            FeatureHub(Option(87), Option(15), Option(3)),
            FeatureHub(Option(88), Option(15), Option(4)),
            FeatureHub(Option(89), Option(15), Option(5)),
            FeatureHub(Option(90), Option(15), Option(6)),
            FeatureHub(Option(91), Option(15), Option(7))


          ).foreach(FeaturesHub.insert)

            Seq(
              Addon(Option(1), "Гавайский массаж Lomi-Lomi 1.5 часа", 3000, "images/addons/addon1.html"),
              Addon(Option(2), "Романтический ужин на двоих при свечах в номере", 2300, "images/addons/addon2.html"),
              Addon(Option(3), "СПА-Педикюр", 2000, "images/addons/addon3.html")
            ).foreach(Addons.insert)

        }
      }
    }
  }


}

