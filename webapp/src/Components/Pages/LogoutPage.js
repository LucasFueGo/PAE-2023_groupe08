import {clearAuthenticatedUser} from "../../utils/auths";
import Navigate from "../Router/Navigate";
import Navbar from "../Navbar/Navbar";

const LogoutPage = () => {
  clearAuthenticatedUser();
  Navbar();
  Navigate('/connexion');
}

export default LogoutPage;
