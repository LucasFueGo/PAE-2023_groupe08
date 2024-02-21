
import Navbar from "../Navbar/Navbar";
import gif from "../../img/loader2.gif";

const ObjectPage = () => {
  const main = document.querySelector('main .row');
  main.innerHTML = '';
  Navbar();
    const urlString = window.location.href;
    const url = new URL(urlString);
    const idObject = url.searchParams.get("IdObject");
    detectGetValue(idObject);
}

function detectGetValue(idInParam) {
  const main = document.querySelector('main .row');
  const idObject = idInParam;
  if(idObject === null) {
    main.innerHTML = "<div class='row justify-content-center'><div class='col-12 text-center'><h2>Une erreur est survenue lors du chargement de la page</h2></div></div>";
  } else {
    main.innerHTML = `<div class='row justify-content-center'><div class='col-12 text-center'><img src="${gif}" alt="loader"></div></div>`;
    apiCallObjectId(idObject).then((object) => {
      diplayObject(object);
    });
  }
}

async function apiCallObjectId(idObject) {
  const response = await fetch(`http://localhost:8080/object/getOne/${idObject}`);
  if (!response.ok) throw new Error(
      `fetch error : ${response.status} : ${response.statusText}`);
  return response.json();
}

function diplayObject(object) {
  const main = document.querySelector('main .row');
  const dateDepose = new Date(object.datePassage);
  let dateAccepted;
  let htmlDateAcceptation;
  if(object.acceptedDate === null){
    htmlDateAcceptation = ""
  } else {
    dateAccepted = new Date(object.acceptedDate);
    htmlDateAcceptation = `<p><b>Date d'acceptation : ${dateAccepted.toLocaleDateString("en-GB")}</b></p>`;
  }

  main.innerHTML = `
<div class="row justify-content-center">
  <div class="col-10">
    <div class="row justify-content-center align-items-center" style="height:80vh;">
      <div class="col-12 col-md-6 content-all-object">
        <div class="image_object link-object">
          <span class="info_object_disponibility ${object.state}">${object.stateComplete}</span>
          <img class="image-object card-img-top d-block m-auto" data-id="${object.id}" src="${object.url == null ? 'https://plchldr.co/i/500x350?bg=111111' : object.url}" >
        </div>
      </div>
      <div class="col-12 col-md-6">
        <h1>${object.description}</h1>
        <h6>${object.nameType}</h6>
        <p><b>Date de dépôt de l'objet : ${dateDepose.toLocaleDateString("en-GB")}</b></p>
        ${htmlDateAcceptation}
        <p><b>Prix : ${object.sellingPrice} €</b></p>
      </div>
    </div>
  </div>
</div>`;
}


export default ObjectPage;