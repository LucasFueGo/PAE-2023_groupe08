
import Navbar from "../Navbar/Navbar";
import gif from "../../img/loader2.gif";
import {getAuthenticatedUser, getToken} from "../../utils/auths";
import Navigate from "../Router/Navigate";
import ObjectPage from "./ObjectPage";

const ProposePage = () => {
  const main = document.querySelector('main .row');
  main.innerHTML = `<div class='row justify-content-center'><div class='col-12 text-center'><img src="${gif}" alt="loader"></div></div>`;
  Navbar();
  diplayField();

}

function diplayField() {
  const user = getAuthenticatedUser();
  const main = document.querySelector('main .row');
  main.innerHTML = `
    <div class="container rounded mt-5 mb-5">
        <div class="row justify-content-center">
          <div class="col-12 col-lg-10 content-all-object row justify-content-center propose-new-object">
            <h2 class="text-center mt-5 mb-5">Proposer un objet</h2>
              <div class="col-6">
              <form>
                <label for="image">Image <b class="warning-color">*</b></label>
                <input type="file" accept="image/png, image/jpeg" name="image" class="url form-control">
                <br>
                
               <label for="tel">Numero de téléphone <b class="warning-color">${user === null ? `*` : ``}</b></label>
               <input type="text" name="tel" style="width:100%" class="tel form-control">
               <br>
               <label for="date-depot">Date de dépot <b class="warning-color">*</b></label>
                 <div class="depot-day"></div>
               <br>
              
              </div>
              <div class="col-6">
                <label for="title">Description <b class="warning-color">*</b></label>
              <br>
              <input type="text" name="title" style="width:100%" class="description form-control" maxlength="120" required>
              <br>
                <label for="type" class="mt-3">Type d'objet <b class="warning-color">*</b></label>
              <br>
              <div class="select-update"></div>
              <div class="information-submit mt-3"></div>
              ${user !== null ? `
              <button class="btn rounded-0 py-2 px-3 warning-color-btn btn-propose-object" type="submit">Proposer</button>
              ` : '<p style="font-size: 13px; margin-top: 15px;">Pour proposer un objet vous devez soit être connecté, soit indiquer votre numéro de téléphone.</p>' 
      + '<button class="btn rounded-0 py-2 px-3 background-accent-color btn-propose-object" type="submit">Proposer</button><button class="btn rounded-0 py-2 px-3 ms-3 warning-color-btn btn-to-connect">Je me connecte</button>'
              }
              </div>
              </form>
           </div>
        </div>
    </div>
  `;

  getAllType();
  getAllAvailable();

  const btnPropose = document.querySelector('button.btn-propose-object');
  btnPropose.addEventListener('click', (elem) => {
    elem.preventDefault();
    proposeObject();
  });
  if(user === null){
    const btnToCOnnect = document.querySelector('button.btn-to-connect');
    btnToCOnnect.addEventListener('click', (elem) => {
      elem.preventDefault();
      Navigate('/connexion');
    });
  }

}

async function getAllAvailable() {
  const response = await fetch('http://localhost:8080/available/getAll');

  if (!response.ok) throw new Error(
      `fetch error : ${response.status} : ${response.statusText}`);

  const availables = await response.json();
  const divSelectDay = document.querySelector('div.depot-day');
  divSelectDay.innerHTML = displayAllDay(availables);
}

function displayAllDay(availables){

  let allInfo = `
    <select name="date-depot" style="width:100%" class="date-depot form-control">
    `;

  availables?.forEach((available) => {
    let textTime;
    const dateDepose = new Date(available.day);
    if(available.mornig === true){
      textTime = "de 11h à 13h";
    } else {
      textTime = "de 14h à 16h";
    }
    allInfo += `
      <option value="${available.id}-${available.mornig}">${dateDepose.toLocaleDateString("en-GB")} ${textTime}</option>
    `;
  });

  allInfo += `
    </select>
  `;

  return allInfo;

}

async function getAllType() {
  // futur appel à l'api pour les types de objets

  const response = await fetch('http://localhost:8080/filter/getAll');

  if (!response.ok) throw new Error(
      `fetch error : ${response.status} : ${response.statusText}`);

  const filters = await response.json();

  const divSelectUpdate = document.querySelector('div.select-update');
  divSelectUpdate.innerHTML = displayAllType(filters);

}

function displayAllType(filters){

  let allInfo = `
    <select class="form-select idType">
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

async function proposeObject() {
  const user = getAuthenticatedUser();
  const idTypeInput = document.querySelector('div.propose-new-object select.idType').value;
  const regex = /^\d{4}\/\d{2}\.\d{2}\.\d{2}$/;
  const descriptionInput = document.querySelector(
      'div.propose-new-object input.description').value;

  let image = document.querySelector('div.propose-new-object input.url');

  const tel = document.querySelector('div.propose-new-object input.tel').value;
  const jour = document.querySelector('div.propose-new-object select.date-depot').value;

  const values = jour.split('-');
  const jourID = parseInt(values[0],10);
  let heureID
  if(values[1] === "true"){
    heureID = 1;
  } else {
    heureID = 2;
  }

  const divInfo = document.querySelector('div.information-submit');
  divInfo.innerHTML = "";

  if(image.value === undefined || image.value === null || image.value === "" || !image.value) {
    image = null;
  }
  if(image == null){
    divInfo.innerHTML = "<p style='color:red'>Il faut renseigner une image.</p>";
  }

  if(user === null && !tel){
    divInfo.innerHTML = "<p style='color:red'>Il faut renseigner un numéro de téléphone.</p>";
  }
  if(!descriptionInput){
    divInfo.innerHTML = "<p style='color:red'>Il faut renseigner une description.</p>";
  }

  if(!regex.test(tel) && (user === null || user === undefined)){
    divInfo.innerHTML = "<p style='color:red'>Numéro de téléphone pas dans le bon format (exemple: 0476/96.36.26).</p>";
  }

  if(descriptionInput && image !== null){
    const idDate = jourID;
    const idTimeSlot = heureID;
    const idType = idTypeInput;
    const description = descriptionInput;
    const options = {
      method: 'POST',
      body: {},
      headers: {}
    };

    if(user !== null) {
      const token = getToken();
        options.body = JSON.stringify({
          idDate,
          idTimeSlot,
          idType,
          description,
          token
        })
        options.headers = {
          'Content-Type': 'application/json',
          'Authorization': token,
        }

    } else {
        const phoneNumber = tel;
        options.body = JSON.stringify({
          idDate,
          idTimeSlot,
          idType,
          description,
          phoneNumber
        });
        options.headers = {
          'Content-Type': 'application/json'
        }
    }

    const response = await fetch('http://localhost:8080/object/proposeObject', options);
    if (!response.ok) throw new Error(
        `fetch error : ${response.status} : ${response.statusText}`);

    const id = await response.json();
    const objectEntier = new FormData();
    objectEntier.append('file', image.files[0]);
    objectEntier.append('idObject', id.objectID);

    const options2 = {
      method: 'POST',
      body: objectEntier,
    };

    const response2 = await fetch(`http://localhost:8080/object/upload`, options2);
    if (!response2.ok) throw new Error(
        `fetch error : ${response2.status} : ${response2.statusText}`);
    await response2.json();

    window.history.pushState({}, '', `/object?IdObject=${id.objectID}`);
    ObjectPage(id.objectID);
  }


}

export default ProposePage;