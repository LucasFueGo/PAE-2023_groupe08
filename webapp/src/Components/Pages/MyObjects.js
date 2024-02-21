
import Navbar from "../Navbar/Navbar";
import gif from "../../img/loader2.gif";
import {getAuthenticatedUser, getToken} from "../../utils/auths";
import ObjectPage from "./ObjectPage";

const ProposeObjectsPage = () => {
  const isConnected = getAuthenticatedUser();
  if(isConnected) {
    const main = document.querySelector('main .row');
    main.innerHTML = `
  <div class="info_top">
    <h1 class="text-center mt-5 mb-5">Mes Objets</h1>
  </div>
  <div class="container rounded mt-5 mb-5">
    <div class="row">
      <div class="col-md-12 justify-content-center content-list-propose">
        <div class='col-12 text-center'><img src="${gif}" alt="loader">
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
    headers: {
      'Authorization': getToken()
    }
  }
  const userInfo = getAuthenticatedUser();
  const response = await fetch(`http://localhost:8080/object/getAllObjectsOfAUser/${userInfo.id}`,options);

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
}


function displayObjects(objects) {
  let allInfo;
  if(objects.length === 0){
    allInfo = "<h5 class='text-center'>Vous n'avez pas d'objet.</h5>";
  } else {
    allInfo = '<div class="container">';
    objects?.forEach((object) => {
      const dateDepose = new Date(object.datePassage);
      let dateAccept;
      let htmlInfo;
      if(object.acceptedDate){
        dateAccept = new Date(object.acceptedDate);
        htmlInfo = dateAccept.toLocaleDateString();
      } else {
        htmlInfo = "";
      }

      let textDenial = '';
      if(object.denialNotification !== null){
        textDenial = `<p>Raison du refus : ${object.denialNotification}</p>`;
      }

      allInfo += `
      <div class="single-object link-object col-12 row" data-id="${object.id}" data-idType="${object.idType}" data-uri="/objets?IdObject=${object.id}">
        <div class="col-6 toClick-object" data-id="${object.id}">
          <img style="max-width: 450px!important;" class="image-object" data-id="${object.id}">
        </div>
        
        <div class="col-6">
          <div class="informations_object toClick-object" data-id="${object.id}">
              <span class="info_object_disponibility ${object.state}">${object.stateComplete}</span>
              <h3>${object.description}</h3>
              <h6>${object.nameType}</h6>
              <p><b>Heure et date de dépôt : ${object.arrivalTime}h à ${object.departureTime}h le ${dateDepose.toLocaleDateString()}</b></p>
              <p><b>Date d'acception de l'objet : ${htmlInfo}</b></p>
              <p><b>Prix : ${object.sellingPrice} €</b></p>
              ${textDenial}
          </div>
      </div>
      <hr class="mb-5 mt-5">`;
      getImageByObject(object.id);
    })
  }

  allInfo += '</div>';
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