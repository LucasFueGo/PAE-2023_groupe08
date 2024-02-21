
import Navbar from "../Navbar/Navbar";
import gif from "../../img/loader2.gif";
import ObjectPage from "./ObjectPage";
import {getToken} from "../../utils/auths";

const UpdateObjectPage = (idInParam) => {
  const main = document.querySelector('main .row');
  main.innerHTML = '';
  Navbar();
  if(idInParam === undefined){
    const urlString = window.location.href;
    const url = new URL(urlString);
    const idObject = url.searchParams.get("IdObject");
    detectGetValue(idObject);
  } else {
    detectGetValue(idInParam);
  }

}

function detectGetValue(idInParam) {
  const main = document.querySelector('main .row');
  const idObject = idInParam;
  if(idObject === null) {
    main.innerHTML = "<div class='row justify-content-center'><div class='col-12 text-center'><h2>Une erreur est arrivé lors du chargement de la page</h2></div></div>";
  } else {
    main.innerHTML = `<div class='row justify-content-center'><div class='col-12 text-center'><img src="${gif}" alt="loader"></div></div>`;
    apiCallObjectId(idObject);
  }
}

async function apiCallObjectId(idObject) {
  const response = await fetch(`http://localhost:8080/object/getOne/${idObject}`);

  if (!response.ok) throw new Error(
      `fetch error : ${response.status} : ${response.statusText}`);

  const object = await response.json();
  diplayObject(object);

  const buttonUpdate = document.querySelector('button.btn-update-object');
  buttonUpdate.addEventListener('click', (elem) => {
    elem.preventDefault();
    const objectId = buttonUpdate.dataset?.id;
    updateObject(objectId, object);
  })

}

function diplayObject(object) {
  console.log(object);
  const dateAccepted = new Date(object.acceptedDate);
  const main = document.querySelector('main .row');


  main.innerHTML = `
    <div class="container rounded mt-5 mb-5">
        <div class="row justify-content-center">
          <div class="col-12 col-lg-10 content-all-object row justify-content-center update-object">
            <h2 class="text-center mt-5 mb-5">Modifier un objet</h2>
              <div class="col-6">
              <form>
                <label for="image">Nouvelle image</label>
                <input type="file" value="https://plchldr.co/i/900x500?bg=111111" accept="image/png, image/jpeg" name="image" class="url form-control">
                <br>
                <p class="mt-5">Actuellement :</p>
                <img src="" alt="image de l'object ${object.description}" class="image-object">
              </div>
              <div class="col-6">
              <label for="title">Description</label>
              <br>
              <input type="text" value="${object.description}" name="title" style="width:100%" class="description form-control" maxlength="120">
              <br>
              <label for="type" class="mt-3">Type d'objet</label>
              <br>
              <div class="select-update"></div>
              <h6 class="mt-3">Etat de l'objet : ${object.stateComplete}</h6>
              <p><b>Date d'acceptation de l'objet : ${dateAccepted.toLocaleDateString()}</b></p>
              <p><b>Prix : ${object.sellingPrice} €</b></p>
              <button class="btn rounded-0 py-2 px-3 warning-color-btn btn-update-object" type="submit" data-id="${object.id}">Modifier</button>
              </div>
              </form>
           </div>
        </div>
    </div>
  `;

  getImageByObject(object.id);
  getAllType(object.idType);

}

async function getAllType(idType) {
  // futur appel à l'api pour les types de objets

  const response = await fetch('http://localhost:8080/filter/getAll');

  if (!response.ok) throw new Error(
      `fetch error : ${response.status} : ${response.statusText}`);

  const filters = await response.json();

  const divSelectUpdate = document.querySelector('div.select-update');
  divSelectUpdate.innerHTML = displayAllType(filters, idType);

}

async function getImageByObject(id) {
  const response = await fetch(`http://localhost:8080/object/getImageObject/${id}`);

  if (!response.ok) throw new Error(
      `fetch error : ${response.status} : ${response.statusText}`);

  const image = await response.json();

  const imgBalise = document.querySelector('img.image-object');
  imgBalise.setAttribute('src', image.object_image);
}

function displayAllType(filters, idType){

  let allInfo = `
    <select class="form-select idType" value="${idType}">
    `;

  filters?.forEach((filter) => {
    let selected;
    if(filter.id === idType) {
      selected = "selected";
    } else {
      selected = "";
    }
    allInfo += `
      <option value="${filter.id}" ${selected}>${filter.name}</option>
    `;
  });

  allInfo += `
    </select>
  </div>
  `;

  return allInfo;

}

async function updateObject(idObject) {
  const idType = document.querySelector('div.update-object select.idType').value;

  const description = document.querySelector(
      'div.update-object input.description').value;
  let image = document.querySelector('div.update-object input.url');
  if(image.value === undefined || image.value === null || image.value === "" || !image.value) {
    image = null;
  }

  const objectEntier = new FormData();
  objectEntier.append('idObject', idObject);
  objectEntier.append('description', description);
  if(image === null){
    objectEntier.append('file', null);
  } else {
    objectEntier.append('file', image.files[0]);
  }
  objectEntier.append('idType', idType);


  const options = {
    method: 'POST',
    body: objectEntier,
    headers: {
      'Authorization': getToken()
    }
  };

  const response = await fetch('http://localhost:8080/object/updateObject', options);
  if (!response.ok) throw new Error(
      `fetch error : ${response.status} : ${response.statusText}`);

  window.history.pushState({}, '', `/object?IdObject=${idObject}`);
  ObjectPage(idObject);
}



export default UpdateObjectPage;