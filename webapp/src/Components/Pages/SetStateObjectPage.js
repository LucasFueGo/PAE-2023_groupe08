
import Navbar from "../Navbar/Navbar";
import gif from "../../img/loader2.gif";
import {getAuthenticatedUser, getToken} from "../../utils/auths";
import UpdateObjectPage from "./UpdateObjectPage";
import ObjectPage from "./ObjectPage";

const SetStateObjectPage = () => {
  const isConnected = getAuthenticatedUser();
  if(isConnected) {
    const main = document.querySelector('main .row');
    const user = getAuthenticatedUser();
    main.innerHTML = `
  <div class="info_top d-flex" style="height:80vh;">
          <div class="col-auto col-md-3 col-xl-2 px-sm-2 px-0 border-end border-1">
            <div class="d-flex flex-column align-items-center align-items-sm-start px-3 pt-2">
                <ul id="navbarWrapper" class="navbar-nav nav-pills flex-column mb-sm-auto mb-0 align-items-center align-items-sm-start" id="menu">
                 ${user.role === 2 ?  `<li class="nav-item"><a href="/tableau-de-bord" class="nav-link" data-uri="/tableau-de-bord"><i class="fa-solid fa-gauge"></i> Gestion utilisateur et objet</a></li>
                <li class="nav-item"><a href="/gestion-des-objets" class="nav-link" data-uri="/setStateObjects"><i class="fa-solid fa-clipboard-list"></i> Gérer les objets</a></li>
                <li class="nav-item"><a href="/statistiques" class="nav-link" data-uri="/statistics"><i class="fa-solid fa-chart-simple"></i> Tableau de bord</a></li>
<li class="nav-item"><a href="/objets-proposes" class="nav-link" data-uri="/objectsProposes"><i class="fa-solid fa-list-check"></i> Objets proposés</a></li>`:
        `<li class="nav-item"><a href="/gestion-des-objets" class="nav-link" data-uri="/setStateObjects"><i class="fa-solid fa-clipboard-list"></i> Gérer les objets</a></li>`}
                </ul>
            </div>
        </div>
        <div class="col ps-5" style="overflow:auto;">
            <h1 class="mt-5 mb-5">Gérer les objects</h1>
  <div class="container rounded mt-5 mb-5">
    <div class="row">
       <h5>Filtres</h5>
      <div class="col-md-12 content-filter row">
        <div class="col-6 mb-3 content-price-input">
          <div class="w-50">
            <label for="priceMin">Prix minimum</label>
            <input type="number" name="priceMin" class="input form-control priceMin" min="0" style="max-width: 200px">
          </div>
          <div class="w-50">
            <label for="priceMin">Prix Maximum</label>
            <input type="number" name="priceMax" class="input form-control priceMax" min="1" style="max-width: 200px">
          </div>
        </div>
        <div class="col-6 mb-3">
        <label for="priceMin">Type d'objet</label>
         <div class="select-filter"></div>
        </div>
        <hr>
        <div class="col-4 mb-3">
          <label for="priceMin">Objet à aller chercher à une date</label>
          <input type="date" class="input form-control date-filter" name="priceMin">
        </div>
      </div>
      <div class="col-md-12 justify-content-center content-list-propose mt-3">
        <div class='col-12 text-center'><img src="${gif}" alt="loader">
      </div>
    </div>
</div>
  </div>`;
    Navbar();
    getAllType();
    apiCallObjectId();
    detectPriceChange();

    const dateInput = document.querySelector('input.date-filter');
    dateInput.addEventListener('change', (elem) => {
      elem.preventDefault();
      const valueDate = dateInput.value.split("-");
      const newDate = new Date(valueDate[0], valueDate[1]-1, valueDate[2]);

      const objects = document.querySelectorAll('div.single-object');
      /* eslint-disable no-param-reassign */
      objects.forEach((obj) => {
        obj.style.display = "none";
      })
      objects.forEach((obj) => {
       if(Number(obj.dataset?.datepassage) === Number(newDate.getTime())){
         obj.style.display = "flex";
       }
      })
    })

  } else {
    const main = document.querySelector('main .row');
    main.innerHTML = `<div class="info_top">
    <h1 class="text-center mt-5 mb-5">Vous n'avez pas accès à cette ressource.</h1>
    </div>`
  }

}

async function apiCallObjectId() {
  const response = await fetch(`http://localhost:8080/object/getAll`);

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


  const updatebutton = document.querySelectorAll('i.update-button');
  updatebutton.forEach((objectToUpdate) => {
    objectToUpdate.addEventListener('click', (elem) => {
      elem.preventDefault();
      const objectId = objectToUpdate.dataset?.id;
      window.history.pushState({}, '', `/updateObject?IdObject=${objectId}`);
      UpdateObjectPage(objectId);
    })
  });



  const selectStates = document.querySelectorAll('select.select-state');

  selectStates.forEach((selectStateOfObject) => {
    selectStateOfObject.addEventListener('change', (elem) => {
      elem.preventDefault();
      const idObject = selectStateOfObject.dataset?.id;
      if(selectStateOfObject.value === "DW") {
        toWorkshop(idObject);
      } else if (selectStateOfObject.value === "AS") {
        toStore(idObject);
      } else if (selectStateOfObject.value === "PS") {
        toSale(idObject);
      } else if (selectStateOfObject.value === "SO") {
        sold(idObject);
      } else if(selectStateOfObject.value === "WS") {
        retiredSold(idObject);
      }
    })
  })

  const selectStateFromResponsable = document.querySelectorAll('select.select-state-responsable');
  selectStateFromResponsable.forEach((selectStateOfObjectFromResponsable) => {
    selectStateOfObjectFromResponsable.addEventListener('change', (elem) => {
      elem.preventDefault();
      const idObject = selectStateOfObjectFromResponsable.dataset?.id;
      if (selectStateOfObjectFromResponsable.value === "SO") {
        soldFromResponsable(idObject);
      } else if(selectStateOfObjectFromResponsable.value === "PS") {
        toSale(idObject);
      }
    })
  })
}

async function soldFromResponsable(objectID) {
  const textAlertObject = document.querySelector(`p.alert-message[data-id="${objectID}"]`);
  textAlertObject.innerHTML = "";

  const inputPrice = document.querySelector(`input.price_object[data-id="${objectID}"]`);
  const buttonChangePrice = document.querySelector(`button.change_price[data-id="${objectID}"]`);

  inputPrice.classList.remove("d-none");
  buttonChangePrice.classList.remove("d-none");

  buttonChangePrice.addEventListener('click', async (elem) => {
    elem.preventDefault();
    const newPrice = parseFloat(inputPrice.value);

    const options = {
      method: 'POST',
      body: JSON.stringify({
        price: newPrice
      }),

      headers: {
        'Content-Type': 'application/json',
        'Authorization': getToken()
      },
    };

    const response = await fetch(
        `http://localhost:8080/object/toSoldDirectly/${objectID}`, options); // fetch return a promise => we wait for the response

    if (!response.ok) {
      textAlertObject.innerHTML = "Une erreur est survenue";
      throw new Error(
          `fetch error : ${response.status} : ${response.statusText}`)
    }
    ;
    textAlertObject.innerHTML = "L'etat de cette objet est maintenant : Vendu";
    const spanImage = document.querySelector(
        `span.info_object_disponibility[data-id="${objectID}"]`);
    spanImage.innerHTML = "Vendu";
    spanImage.classList.remove("info_available");
    spanImage.classList.remove("info_propose");
    spanImage.classList.add("info_sell");
  })
}

async function toWorkshop(objectID) {

  const textAlertObject = document.querySelector(`p.alert-message[data-id="${objectID}"]`);
  textAlertObject.innerHTML = "";

  const options = {
    method: 'POST',
    body: JSON.stringify({

    }),

    headers: {
      'Content-Type': 'application/json',
      'Authorization': getToken()
    },
  };

  const response = await fetch(`http://localhost:8080/object/toWorkShop/${objectID}`, options); // fetch return a promise => we wait for the response

  if (!response.ok) {
    textAlertObject.innerHTML = "Une erreur est survenue";
    throw new Error(`fetch error : ${response.status} : ${response.statusText}`)
  };
  textAlertObject.innerHTML = "L'etat de cette objet est maintenant : Déposé en atelier";
  const spanImage = document.querySelector(`span.info_object_disponibility[data-id="${objectID}"]`);
  spanImage.innerHTML = "Déposé en atelier";
  spanImage.classList.remove("info_available");
  spanImage.classList.remove("info_sell");
  spanImage.classList.add("info_propose");

}

async function retiredSold(objectID) {

  const textAlertObject = document.querySelector(`p.alert-message[data-id="${objectID}"]`);
  textAlertObject.innerHTML = "";

  const options = {
    method: 'POST',

    headers: {
      'Content-Type': 'application/json',
      'Authorization': getToken()
    },
  };

  const response = await fetch(`http://localhost:8080/object/withdrawObject/${objectID}`, options); // fetch return a promise => we wait for the response

  if (!response.ok) {
    textAlertObject.innerHTML = "Une erreur est survenue";
    throw new Error(`fetch error : ${response.status} : ${response.statusText}`)
  };

  textAlertObject.innerHTML = "L'etat de cette objet est maintenant : Retirer de la vente";
  const spanImage = document.querySelector(`span.info_object_disponibility[data-id="${objectID}"]`);
  spanImage.innerHTML = "Retirer de la vente";
  spanImage.classList.remove("info_available");
  spanImage.classList.remove("info_propose");
  spanImage.classList.add("info_sell");



}

async function toStore(objectID) {

  const textAlertObject = document.querySelector(`p.alert-message[data-id="${objectID}"]`);
  textAlertObject.innerHTML = "";

  const options = {
    method: 'POST',
    body: JSON.stringify({

    }),

    headers: {
      'Content-Type': 'application/json',
      'Authorization': getToken()
    },
  };

  const response = await fetch(`http://localhost:8080/object/toStore/${objectID}`, options); // fetch return a promise => we wait for the response

  if (!response.ok) {
    textAlertObject.innerHTML = "Une erreur est survenue";
    throw new Error(`fetch error : ${response.status} : ${response.statusText}`)
  };
  textAlertObject.innerHTML = "L'etat de cette objet est maintenant : Arrivé en magasin";
  const spanImage = document.querySelector(`span.info_object_disponibility[data-id="${objectID}"]`);
  spanImage.innerHTML = "Arrivé en magasin";
  spanImage.classList.remove("info_propose");
  spanImage.classList.remove("info_sell");
  spanImage.classList.add("info_available");
}

async function toSale(objectID) {

  const textAlertObject = document.querySelector(`p.alert-message[data-id="${objectID}"]`);
  textAlertObject.innerHTML = "";

  const inputPrice = document.querySelector(`input.price_object[data-id="${objectID}"]`);
  const buttonChangePrice = document.querySelector(`button.change_price[data-id="${objectID}"]`);

  inputPrice.classList.remove("d-none");
  buttonChangePrice.classList.remove("d-none");

  buttonChangePrice.addEventListener('click', async (elem) => {
    elem.preventDefault();
    const newPrice = parseFloat(inputPrice.value);

    const options = {
      method: 'POST',
      body: JSON.stringify({
        price: newPrice
      }),

      headers: {
        'Content-Type': 'application/json',
        'Authorization': getToken()
      },
    };

    const response = await fetch(
        `http://localhost:8080/object/toSale/${objectID}`, options); // fetch return a promise => we wait for the response

    if (!response.ok) {
      textAlertObject.innerHTML = "Une erreur est survenue";
      throw new Error(
          `fetch error : ${response.status} : ${response.statusText}`)
    }
    ;
    textAlertObject.innerHTML = "L'etat de cette objet est maintenant : Mis en vente";
    const spanImage = document.querySelector(
        `span.info_object_disponibility[data-id="${objectID}"]`);
    spanImage.innerHTML = "Mis en vente";
    spanImage.classList.remove("info_propose");
    spanImage.classList.remove("info_sell");
    spanImage.classList.add("info_available");

  })



}

async function sold(objectID) {

  const textAlertObject = document.querySelector(`p.alert-message[data-id="${objectID}"]`);
  textAlertObject.innerHTML = "";

  const options = {
    method: 'POST',
    body: JSON.stringify({

    }),

    headers: {
      'Content-Type': 'application/json',
      'Authorization': getToken()
    },
  };

  const response = await fetch(`http://localhost:8080/object/toSold/${objectID}`, options); // fetch return a promise => we wait for the response

  if (!response.ok) {
    textAlertObject.innerHTML = "Une erreur est survenue";
    throw new Error(`fetch error : ${response.status} : ${response.statusText}`)
  };
  textAlertObject.innerHTML = "L'etat de cette objet est maintenant : Vendu";
  const spanImage = document.querySelector(`span.info_object_disponibility[data-id="${objectID}"]`);
  spanImage.innerHTML = "Vendu";
  spanImage.classList.remove("info_available");
  spanImage.classList.remove("info_propose");
  spanImage.classList.add("info_sell");

}

function displayObjects(objects) {
  let allInfo;
  if(objects.length === 0){
    allInfo = "<h5 class='text-center'>No objects to manage</h5>";
  } else {
    allInfo = "";
    objects?.forEach((object) => {
      const dateDepose = new Date(object.datePassage);
      let dateAccepted;
      let htmlDateAcceptation;
      if(object.acceptedDate === null){
        htmlDateAcceptation = ""
      } else {
        dateAccepted = new Date(object.acceptedDate);
        htmlDateAcceptation = `<p><b>Date d'acceptation : ${dateAccepted.toLocaleDateString("en-GB")}</b></p>`;
      }


      let buttonUpdate;
      if(object.state !== "RE") {
        buttonUpdate = `<i class="fa-solid fa-pen-to-square update-button"
           data-id="${object.id}"></i>`;
      } else {
        buttonUpdate = "";
      }

      allInfo += `
      <div class="single-object link-object col-12 row" data-id="${object.id}" data-idType="${object.idType}" data-price="${object.sellingPrice}" data-datepassage="${object.datePassage}" data-uri="/objets?IdObject=${object.id}">
        ${buttonUpdate}
        <div class="col-6 toClick-object" data-id="${object.id}">
          <img style="max-width: 450px!important;" class="image-object" data-id="${object.id}">
        </div>
        
        <div class="col-6">
          <div class="informations_object toClick-object" data-id="${object.id}">
              <span class="info_object_disponibility ${object.state}" data-id="${object.id}">${object.stateComplete}</span>
              <h3>${object.description}</h3>
              <h6>${object.nameType}</h6>
              ${htmlDateAcceptation}
              <p><b>Heure et date de dépôt : ${object.arrivalTime}h à ${object.departureTime}h le ${dateDepose.toLocaleDateString("en-GB")}</b></p>
              <p><b>${object.sellingPrice} €</b></p>
          </div>
          
          ${displaySelectOption(object)}

        </div>
        <hr class="mb-5 mt-5">
      </div>
    `;

      getImageByObject(object.id);
    })
  }


  return allInfo;
}

function displaySelectOption(object) {
  const user = getAuthenticatedUser();
  if (object.state !== "RE") {
    if (object.state === "AC") {
      return `
          <div class="change-state-buttons">
            <h5>Changer l'état de l'object : </h5>
            <div class="row col-12">
          <select class="form-select select-state" ariaclassNamel="Default select example" data-id="${object.id}">
          <option value="AC" selected>Accepté</option>
          <option value="DW">Dépose en atelier</option>
          <option value="AS">Arrivé en magasin</option>
        </select>
            <p class="alert-message" data-id="${object.id}"></p>
            <input type="text" class="price_object d-none" data-id="${object.id}">
            <button class="change_price d-none" data-id="${object.id}">Envoyer</button>
          </div>
          </div>
    `;
    }

    if (object.state === "DW") {
      return `
          <div class="change-state-buttons">
            <h5>Changer l'état de l'object : </h5>
            <div class="row col-12">
          <select class="form-select select-state" ariaclassNamel="Default select example" data-id="${object.id}">
          <option value="DW" selected>Dépose en atelier</option>
          <option value="AS">Arrivé en magasin</option>
        </select>
            <p class="alert-message" data-id="${object.id}"></p>
            <input type="text" class="price_object d-none" data-id="${object.id}">
            <button class="change_price d-none" data-id="${object.id}">Envoyer</button>
          </div>
          </div>
    `;
    }
    if (object.state === "AS") {
      if(user.role === 2){
        return `
          <div class="change-state-buttons">
            <h5>Changer l'état de l'object : </h5>
            <div class="row col-12">
          <select class="form-select select-state-responsable" ariaclassNamel="Default select example" data-id="${object.id}">
         <option value="AS" selected>Arrivé en magasin</option>
         <option value="PS">Mis en vente</option>
         <option value="SO">Vendu</option>
        </select>
            <p class="alert-message" data-id="${object.id}"></p>
            <input type="text" class="price_object d-none" data-id="${object.id}">
            <button class="change_price d-none" data-id="${object.id}">Envoyer</button>
          </div>
          </div>
    `;
      }
        return `
          <div class="change-state-buttons">
            <h5>Changer l'état de l'object : </h5>
            <div class="row col-12">
          <select class="form-select select-state" ariaclassNamel="Default select example" data-id="${object.id}">
         <option value="AS" selected>Arrivé en magasin</option>
         <option value="PS">Mis en vente</option>
        </select>
            <p class="alert-message" data-id="${object.id}"></p>
            <input type="text" class="price_object d-none" data-id="${object.id}">
            <button class="change_price d-none" data-id="${object.id}">Envoyer</button>
          </div>
          </div>
    `;

    }
    if (object.state === "PS") {
      return `
          <div class="change-state-buttons">
            <h5>Changer l'état de l'object : </h5>
            <div class="row col-12">
          <select class="form-select select-state" ariaclassNamel="Default select example" data-id="${object.id}">
         <option value="PS" selected>Mis en vente</option>
         <option value="WS">Retiré de la vente</option>
         <option value="SO">Vendu</option>
        </select>
            <p class="alert-message" data-id="${object.id}"></p>
            <input type="text" class="price_object d-none" data-id="${object.id}">
            <button class="change_price d-none" data-id="${object.id}">Envoyer</button>
          </div>
          </div>
    `;
    }
    if (object.state === "WS") {
      return `
          <div class="change-state-buttons">
            <h5>Changer l'état de l'object : </h5>
            <div class="row col-12">
          <select class="form-select select-state" ariaclassNamel="Default select example" data-id="${object.id}">
         <option value="WS" selected>Retiré de la vente</option>
        </select>
            <p class="alert-message" data-id="${object.id}"></p>
            <input type="text" class="price_object d-none" data-id="${object.id}">
            <button class="change_price d-none" data-id="${object.id}">Envoyer</button>
          </div>
          </div>
    `;
    }
    if (object.state === "SO") {
      return `
          <div class="change-state-buttons">
            <h5>Changer l'état de l'object : </h5>
            <div class="row col-12">
          <select class="form-select select-state" ariaclassNamel="Default select example" data-id="${object.id}">
         <option value="SO" selected>Vendu</option>
        </select>
            <p class="alert-message" data-id="${object.id}"></p>
            <input type="text" class="price_object d-none" data-id="${object.id}">
            <button class="change_price d-none" data-id="${object.id}">Envoyer</button>
          </div>
          </div>
    `;
    }
  }

  return '';
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

async function getAllType() {
  // futur appel à l'api pour les types de objets

  const response = await fetch('http://localhost:8080/filter/getAll');

  if (!response.ok) throw new Error(
      `fetch error : ${response.status} : ${response.statusText}`);

  const filters = await response.json();

  const divSelectUpdate = document.querySelector('div.select-filter');
  divSelectUpdate.innerHTML = displayAllType(filters);


  const selectType = document.querySelector('div.select-filter select');
  const inputMin = document.querySelector('input.priceMin');
  const inputMax = document.querySelector('input.priceMax');
  selectType.addEventListener('change', (elem) => {
    elem.preventDefault();
    const objects = document.querySelectorAll('div.single-object');

    /* eslint-disable no-param-reassign */
    objects.forEach((obj) => {
      obj.style.display = "none";
    })

    objects.forEach((obj) => {
      if (Number(obj.dataset?.idtype) === Number(selectType.value)) {

        if(inputMin.value && inputMax.value) {
          if(Number(obj.dataset?.price) >= parseInt(inputMin.value,10) && Number(obj.dataset?.price) <= parseInt(inputMax.value,10)){
            obj.style.display = "flex";
          }
        } else {
          obj.style.display = "flex";
        }

      } else if (Number(selectType.value) === 0) {
        if(inputMin.value && inputMax.value) {
          if(Number(obj.dataset?.price) >= parseInt(inputMin.value,10) && Number(obj.dataset?.price) <= parseInt(inputMax.value,10)){
            obj.style.display = "flex";
          }
        } else {
          obj.style.display = "flex";
        }
      }
    })

  })


}

function displayAllType(filters){

  let allInfo = `
    <select class="form-select idType">
    <option value="0">Tout les types</option>
    `;

  filters?.forEach((filter) => {
    allInfo += `
      <option value="${filter.id}">${filter.name}</option>
    `;
  });

  allInfo += `
    </select>
  </div>
  `;

  return allInfo;

}

function detectPriceChange() {
  const inputMin = document.querySelector('input.priceMin');
  const inputMax = document.querySelector('input.priceMax');

  inputMin.addEventListener('change', (elem) => {
    elem.preventDefault();
    if(Number(inputMax.value) <= Number(inputMin.value)){
      inputMax.value = parseInt(inputMin.value,10)+1;
    }

    const objects = document.querySelectorAll('div.single-object');
    const selectType = document.querySelector('div.select-filter select');
    /* eslint-disable no-param-reassign */
    objects.forEach((obj) => {
      obj.style.display = "none";
    })

    objects.forEach((obj) => {
      if (Number(obj.dataset?.price) >= parseInt(inputMin.value,10) && Number(obj.dataset?.price) < parseInt(inputMax.value,10)) {
        if(Number(selectType.value) > 0) {
          if(Number(selectType.value) === Number(obj.dataset?.idtype)) {
            obj.style.display = "flex";
          }
        } else {
          obj.style.display = "flex";
        }
      } else if(!inputMin.value && !inputMax.value){
        obj.style.display = "flex";
      }
    })


  })

  inputMax.addEventListener('change', (elem) => {
    elem.preventDefault();

    if(parseInt(inputMax.value,10) <= parseInt(inputMin.value,10)){
      inputMax.value = parseInt(inputMin.value,10)+1;
    }

    const objects = document.querySelectorAll('div.single-object');
    const selectType = document.querySelector('div.select-filter select');
    /* eslint-disable no-param-reassign */
    objects.forEach((obj) => {
      obj.style.display = "none";
    })
    objects.forEach((obj) => {
      if (Number(obj.dataset?.price) >= parseInt(inputMin.value,10) && Number(obj.dataset?.price) <= parseInt(inputMax.value,10)) {
        if(Number(selectType.value) > 0) {
          if(Number(selectType.value) === Number(obj.dataset?.idtype)) {
            obj.style.display = "flex";
          }
        } else {
          obj.style.display = "flex";
        }
      } else if(!inputMin.value && !inputMax.value){
        obj.style.display = "flex";
      }
    })
  })
}

export default SetStateObjectPage;