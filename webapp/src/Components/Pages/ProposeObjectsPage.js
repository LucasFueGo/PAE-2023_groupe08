
import Navbar from "../Navbar/Navbar";
import gif from "../../img/loader2.gif";
import {getAuthenticatedUser, getToken} from "../../utils/auths";
import ObjectPage from "./ObjectPage";

const ProposeObjectsPage = () => {
  const isConnected = getAuthenticatedUser();
  if(isConnected) {
    const user = getAuthenticatedUser();
    const main = document.querySelector('main .row');
    main.innerHTML = `
  <div class="info_top d-flex" style="height:80vh;">
            <div class="col-auto col-md-3 col-xl-2 px-sm-2 px-0 border-end border-1">
            <div class="d-flex flex-column align-items-center align-items-sm-start px-3 pt-2">
                <ul id="navbarWrapper" class="navbar-nav nav-pills flex-column mb-sm-auto mb-0 align-items-center align-items-sm-start" id="menu">
                <ul id="navbarWrapper" class="navbar-nav nav-pills flex-column mb-sm-auto mb-0 align-items-center align-items-sm-start" id="menu">
                 ${user.role === 2 ?  `<li class="nav-item"><a href="/tableau-de-bord" class="nav-link" data-uri="/tableau-de-bord"><i class="fa-solid fa-gauge"></i> Gestion utilisateur et objet</a></li>
                <li class="nav-item"><a href="/gestion-des-objets" class="nav-link" data-uri="/setStateObjects"><i class="fa-solid fa-clipboard-list"></i> Gérer les objets</a></li>
                <li class="nav-item"><a href="/statistiques" class="nav-link" data-uri="/statistics"><i class="fa-solid fa-chart-simple"></i> Tableau de bord</a></li>
                <li class="nav-item"><a href="/objets-proposes" class="nav-link" data-uri="/objectsProposes"><i class="fa-solid fa-list-check"></i> Objets proposés</a></li>`:
        `<li class="nav-item"><a href="/gestion-des-objets" class="nav-link" data-uri="/setStateObjects"><i class="fa-solid fa-clipboard-list"></i> Gérer les objets</a></li>`}
                </ul>
            </div>
        </div>
        <div class="col ps-5" style="overflow: auto;">
            <h1 class="mt-5 mb-5">Objets proposés</h1>
  <div class="container rounded mt-5 mb-5">
    <div class="row">
      <div class="col-md-12 justify-content-center content-list-propose">
        <div class='col-12 text-center'><img src="${gif}" alt="loader">
      </div>
    </div>
        
</div>
  </div>`;
    Navbar();
    apiCallObjectId();
  } else {
    const main = document.querySelector('main .row');
    main.innerHTML = `<div class="info_top">
    <h1 class="text-center mt-5 mb-5">Vous n'avez pas accès à cette ressource.</h1>
    </div>`
  }
}

async function apiCallObjectId() {
  const options = {
    method: 'GET',
    headers: {
      'Authorization': getToken(),
    },
  };

  const response = await fetch(`http://localhost:8080/object/getAllObjectPropose`, options);

  if (!response.ok) throw new Error(
      `fetch error : ${response.status} : ${response.statusText}`);

  const objects = await response.json();

  const main = document.querySelector('div.content-list-propose');
  main.innerHTML = displayObjects(objects);


  const objectsLinks = document.querySelectorAll('div.toClick-object');

  objectsLinks.forEach((linkObj) => {
    const objectLinkId = linkObj.dataset?.id;
    linkObj.setAttribute("data-uri",  `/objets?IdObject=${objectLinkId}`);
    linkObj.addEventListener('click', (elem) => {
      const uri = linkObj.dataset?.uri;
      elem.preventDefault();
      const path = uri.split("?")[0];
      if (path) {
        const urlParams = new URLSearchParams(uri.split("?")[1]);
        const idObject = urlParams.get("IdObject");
        window.history.pushState({}, '', `/objets?IdObject=${idObject}`);
        ObjectPage(idObject);
      }
    })
  });


  const declinePropose = document.querySelectorAll('button.decline-propose');

  declinePropose.forEach((proposeObject) => {
    proposeObject.addEventListener('click', (elem) => {
      elem.preventDefault();
      const idObject = proposeObject.dataset?.id;
      const texteArea = document.querySelector(`.decline-propose-text[data-id="${idObject}"]`);
      texteArea.style.display = "block";
    })
  })

  const badPropose = document.querySelectorAll('button.sendBadPropose');

  badPropose.forEach((declineObject) => {
    declineObject.addEventListener('click', (elem) => {
      elem.preventDefault();
      const idObject = declineObject.dataset?.id;
      const texteArea = document.querySelector(`textarea[data-id="${idObject}"]`);
      const textAlertPropose = document.querySelector(`.decline-propose-text[data-id="${idObject}"] p.alert-message`);
      if(texteArea.value === ''){
        textAlertPropose.innerHTML = "Ce champs est obligatoire.";
      } else{
        textAlertPropose.innerHTML = "";
        refuseObject(idObject, texteArea.value);
      }
    })
  })

  const acceptePropose = document.querySelectorAll('button.accepte-propose');
  acceptePropose.forEach((acceptedObject) => {
    acceptedObject.addEventListener('click', (elem) => {
      elem.preventDefault();
      const idObject = acceptedObject.dataset?.id;
      accepteObject(idObject);
    })
  })
}

async function accepteObject(objectID) {
  const textAlertPropose = document.querySelector(`.decline-propose-text[data-id="${objectID}"] p.alert-message`);

  const options = {
    method: 'POST',
    body: JSON.stringify({

    }),

    headers: {
      'Content-Type': 'application/json',
      'Authorization': getToken()
    },
  };

  const response = await fetch(`http://localhost:8080/object/toAccept/${objectID}`, options); // fetch return a promise => we wait for the response

  if (!response.ok) {
    textAlertPropose.innerHTML = "An error has occurred";
    throw new Error(`fetch error : ${response.status} : ${response.statusText}`)
  };
  ProposeObjectsPage();
}

async function refuseObject(objectID, texteRefuse) {
  const textAlertPropose = document.querySelector(`.decline-propose-text[data-id="${objectID}"] p.alert-message`);

  const options = {
    method: 'POST',
    body: JSON.stringify({
      refusal_notification : texteRefuse
    }),

    headers: {
      'Content-Type': 'application/json',
      'Authorization': getToken()
    },
  };

  const response = await fetch(`http://localhost:8080/object/refuse/${objectID}`, options); // fetch return a promise => we wait for the response

  if (!response.ok) {
    textAlertPropose.innerHTML = "An error has occurred";
    throw new Error(`fetch error : ${response.status} : ${response.statusText}`)
  };
  ProposeObjectsPage();
}




function displayObjects(objects) {
  let allInfo;
  if(objects.length === 0){
    allInfo = "<h5 class='p-0 m-0'>Pas d'objets proposés pour le moment.</h5>";
  } else {
    allInfo = "";
    objects?.forEach((object) => {

      const dateDepose = new Date(object.datePassage);

      allInfo += `
      <div class="single-object link-object col-12 row" data-id="${object.id}" data-idType="${object.idType}">
        <div class="col-6 toClick-object" data-id="${object.id}">
          <img alt="plchldr.co" style="max-width: 450px!important;" class="image-object" data-id="${object.id}">
        </div>
        
        <div class="col-6">
          <div class="informations_object toClick-object" data-id="${object.id}">
              <span class="info_object_disponibility ${object.state}">${object.stateComplete}</span>
              <h3>${object.description}</h3>
              <h6>${object.nameType}</h6>
              <p><b>Heure et date de dépôt : ${object.arrivalTime}h à ${object.departureTime}h le ${dateDepose.toLocaleDateString()}</b></p>
              <p><b>Prix : ${object.sellingPrice} €</b></p>
          </div>
          
          <div class="propose-buttons">
            <button type="submit" class="btn rounded-0 py-2 px-3 warning-color-btn accepte-propose" data-id="${object.id}">Accepter</button>
            <button type="submit" class="btn rounded-0 py-2 px-3 decline-propose" data-id="${object.id}">Refuser</button>
            <br>
            <div class="decline-propose-text mt-2 text-end" data-id="${object.id}" style="display: none">
              <textarea placeholder="Entrez une explication concernant votre refus." data-id="${object.id}"></textarea>
              <p class="alert-message"></p>
              <br>
              <button type="submit" class="btn rounded-0 py-2 px-3 sendBadPropose" style="border: 1px solid black;" data-id="${object.id}">Envoyer</button>
            </div>
          </div>
        </div>
      </div>
      <hr class="mb-5">`;
      getImageByObject(object.id);
    })
  }


  return allInfo;
}

async function getImageByObject(id) {
  const response = await fetch(`http://localhost:8080/object/getImageObject/${id}`);

  if (!response.ok) throw new Error(
      `fetch error : ${response.status} : ${response.statusText}`);

  const image = await response.json();

  const imgBalise = document.querySelector(`img.image-object[data-id="${id}"]`);
  if(image.object_image === null || image.object_image === undefined || !image.object_image) {
    image.object_image = "https://plchldr.co/i/500x350?bg=111111";
  }
  imgBalise.setAttribute('src', image.object_image);
}


export default ProposeObjectsPage;