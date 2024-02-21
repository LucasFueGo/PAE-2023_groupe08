import routes from "./routes";
// import ObjectPage from "../Pages/ObjectPage";
import {refreshAndCheckToken} from "../../utils/auths";

const Router = () => {
    onFrontendLoad();
    onNavBarClick();
    // onObjectClick();
    onHistoryChange();
};

function onNavBarClick() {
  const navbarWrapper = document.querySelector('#navbarWrapper');

  navbarWrapper.addEventListener('click', (e) => {
    e.preventDefault();
    const navBarItemClicked = e.target;
    const uri = navBarItemClicked?.dataset?.uri;

    if(uri !== "/deconnexion") {
      refreshAndCheckToken();
    }

    if (uri) {
      const componentToRender = routes[uri];
      if (!componentToRender) throw Error(`The ${uri} ressource does not exist.`);

      componentToRender();
      window.history.pushState({}, '', uri);
    }
  });
}
/*
function onObjectClick() {
  const divStatic = document.querySelector('.object-available');

  divStatic.addEventListener('click', (e) => {
    e.preventDefault();
    const navBarItemClicked = e.target;
    const uri = navBarItemClicked?.dataset?.uri;
    const path = uri.split("?")[0];


    if (path) {
      const componentToRender = routes[path];

      if (!componentToRender) throw Error(`The ${path} ressource does not exist.`);

      componentToRender(uri);


      const urlParams = new URLSearchParams(uri.split("?")[1]);
      const idObject = urlParams.get("IdObject");
      window.history.pushState({}, '', `/objets?IdObject=${idObject}`);
      divStatic.innerHTML = "";
      ObjectPage(idObject);
    }
  });
}

 */

function onHistoryChange() {
  window.addEventListener('popstate', () => {
    const uri = window.location.pathname;
    const componentToRender = routes[uri];
    componentToRender();
  });
}

function onFrontendLoad() {
  window.addEventListener('load', () => {
    const uri = window.location.pathname;
    const componentToRender = routes[uri];
    if (!componentToRender) throw Error(`The ${uri} ressource does not exist.`);

    componentToRender();
  });
}

export default Router;