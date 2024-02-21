import HomePage from '../Pages/HomePage';
import LoginPage from '../Pages/LoginPage';
import LogoutPage from "../Pages/LogoutPage";
import ObjectPage from "../Pages/ObjectPage";
import ProposeObjectsPage from "../Pages/ProposeObjectsPage";
import SetStateObjectPage from "../Pages/SetStateObjectPage";
import DashboardPage from "../Pages/DashboardPage";
import MyObjects from "../Pages/MyObjects";
import ProfilePage from "../Pages/ProfilePage";
import NotificationPage from "../Pages/NotificationPage";
import UpdateObjectPage from '../Pages/UpdateObjectPage';
import StatisticsPage from "../Pages/StatisticsPage";
import ProposePage from "../Pages/ProposePage";

const routes = {
  '/': HomePage,
  '/connexion': LoginPage,
  '/deconnexion': LogoutPage,
  '/profil': ProfilePage,
  '/objets': ObjectPage,
  '/statistiques': StatisticsPage,
  '/objets-proposes' :ProposeObjectsPage,
  '/gestion-des-objets' : SetStateObjectPage,
  '/tableau-de-bord' : DashboardPage,
  '/mes-objets' : MyObjects,
  '/notifications' : NotificationPage,
  '/mise-a-jour' : UpdateObjectPage,
  '/proposer' : ProposePage,
};

export default routes;
