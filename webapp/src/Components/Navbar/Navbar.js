// eslint-disable-next-line no-unused-vars
import {getAuthenticatedUser} from "../../utils/auths";

const Navbar = () => {
  const user = getAuthenticatedUser();
  const navbarWrapper = document.querySelector('#navbarWrapper');
  navbarWrapper.innerHTML = `<nav class="navbar fixed-top bg-light shadow-sm navbar-expand-lg py-3">
        <div class="container-lg">
          <a class="navbar-brand fw-bold" href="/" data-uri="/">RessourceRie</a>
          <button
            class="navbar-toggler"
            type="button"
            data-bs-toggle="collapse"
            data-bs-target="#navbarSupportedContent"
            aria-controls="navbarSupportedContent"
            aria-expanded="false"
            aria-label="Toggle navigation"
          >
            <span class="navbar-toggler-icon"></span>
          </button>
          <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav ms-auto mb-2 mb-lg-0">
              <li class="nav-item">
                <a class="nav-link px-2" href="#" data-uri="/">Accueil</a>
              </li>
              <li class="nav-item">
                <a class="nav-link px-2" href="#" data-uri="/proposer">Proposer un objet</a>
              </li>
              ${user !== null && user.role === 2 ? `
              <li class="nav-item">
                <a class="nav-link px-2" href="#" data-uri="/tableau-de-bord">Espace admin</a>
              </li>` : ''}
              ${user !== null && user.role === 1 ? `<li class="nav-item">
                <a class="nav-link px-2" href="#" data-uri="/gestion-des-objets">Espace admin</a>
              </li>` : ''}
              ${user == null ? `<li class="nav-item"><button class="btn btn-success ms-4 rounded-5" data-uri="/connexion">Connexion/Inscription</button></li>` : `
               <div class="dropdown dropdown-menu-start">
                <button id="dropdown-btn" type="button" class="btn dropdown-toggle" data-bs-toggle="dropdown">
                  <img id="user-profile-img" src="${user.image === null ? 'default.png' : user.image}" alt="image de profil">
                </button>
                <ul class="dropdown-menu">
                  <li><a class="dropdown-item px-2" href="#" data-uri="/profil"><i class="fa-solid fa-gear"></i> Mon profil</a></li>
                  <li><a class="dropdown-item px-2" href="#" data-uri="/mes-objets"><i class="fa-solid fa-list-ul"></i> Mes objets</a></li>
                  <li><a class="dropdown-item px-2" href="#" data-uri="/notifications"><i class="fa-solid fa-bell"></i> Mes notifications</a></li>
                  <li><a class="dropdown-item px-2" href="#" data-uri="/deconnexion"><i class="fa-solid fa-right-from-bracket"></i> Déconnexion</a></li>
                </ul>
              </div>`}              
            </ul>
          </div>
        </div>
      </nav>`;
}

export default Navbar;
