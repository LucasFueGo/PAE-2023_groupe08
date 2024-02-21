// eslint-disable-next-line import/named
import {
  isAuthenticated,
  getToken,
  getAuthenticatedUser
} from "../../utils/auths";
import Navigate from "../Router/Navigate";
import ObjectPage from "./ObjectPage";

const DashboardPage = () => {

  if(!isAuthenticated()){
    Navigate('/');
  }else{
    const main = document.querySelector('main .row');
    const user = getAuthenticatedUser();
    main.innerHTML = `
    <div class="row">
        <div class="col-auto col-md-3 col-xl-2 px-sm-2 px-0 border-end border-1">
            <div class="d-flex flex-column align-items-center align-items-sm-start px-3 pt-2">
                <ul class="navbar-nav nav-pills flex-column mb-sm-auto mb-0 align-items-center align-items-sm-start" id="menu">
                 ${user.role === 2 ?  `<li class="nav-item"><a href="/tableau-de-bord" class="nav-link" data-uri="/tableau-de-bord"><i class="fa-solid fa-gauge"></i> Gestion utilisateur et objet</a></li>
                <li class="nav-item"><a href="/gestion-des-objets" class="nav-link" data-uri="/setStateObjects"><i class="fa-solid fa-clipboard-list"></i> Gérer les objets</a></li>
                <li class="nav-item"><a href="/statistiques" class="nav-link" data-uri="/statistics"><i class="fa-solid fa-chart-simple"></i> Tableau de bord</a></li><li class="nav-item"><a href="/objets-proposes" class="nav-link" data-uri="/objectsProposes"><i class="fa-solid fa-list-check"></i> Objets proposés</a></li>`:
        `<li class="nav-item"><a href="/gestion-des-objets" class="nav-link" data-uri="/setStateObjects"><i class="fa-solid fa-clipboard-list"></i> Gérer les objets</a></li>`}
                </ul>
            </div>
        </div>
        <div class="col">
            <div class="p-3 section-user mt-3" style="min-height: 20vh">
              <h4 class="">Gestion des rôles</h4>
                <div class="d-flex justify-content-between align-items-center col-12 row">
                    <div class="content-all-user col-12"></div>
                    <div class="info-user col-12 row"></div>
                </div>
          </div>
          <div class="p-3 py-5 section-object-user" style="min-height: 20vh">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h4 class="text-right">Gestion des objets (par utilisateur)</h4>
                </div>
                <div class="d-flex justify-content-between align-items-center col-12 row">
                    <div class="content-all-user-object col-12"></div>
                    <div class="info-user-object col-12 row" style="margin-top:10px;"></div>
                </div>
          </div>
          
          <div class="p-3 py-5 samedi-section" style="min-height: 20vh">
              <div class="d-flex justify-content-between align-items-center mb-3">
                  <h4 class="text-right">Indiquer un samedi</h4>
              </div>
              <div class="d-flex justify-content-between align-items-center col-12 row">
                  <div class="input-date-content col-12" style="display: flex">
                    <input type="date" class="input-date input form-control" style="max-width: 300px;margin-right:10px">
                    De
                    <label for="matin">11h-13h
                    <input type="radio" id="matin" name="heure-date" value="1" checked class="radio-time"></label>
                    ou 
                    <label for="aprem">14h-16h
                    <input type="radio" id="aprem" name="heure-date" value="0" class="radio-time"></label>
                  </div>
                  <button class="input-date-btn btn rounded-0 warning-color-btn" style="max-width: 160px;margin-left:13px;margin-top:10px">Indiquer cette date</button>
                  <div class="info-date"></div>
                  <div class="info-user-object col-12 row" style="margin-top:10px;"></div>
              </div>
          </div>
          
    </div>
  </div>
  </div>
  </div>`;
    displayTabUser().then(() => {
      displayObjectByUser().then(() => {
        clickSamedi();
      });
    });

  }
};

const token = getToken();

async function displayObjectByUser() {
  const options = {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': getToken()
    },
  };

  const response = await fetch('http://localhost:8080/users/getAll', options); // fetch return a promise => we wait for the response

  if (!response.ok) throw new Error(`fetch error : ${response.status} : ${response.statusText}`);

  const users = await response.json(); // json() returns a promise => we wait for the data$

  const contentAllUsers = document.querySelector('.content-all-user-object');
  contentAllUsers.innerHTML = displayListUser(users);

  const selectUserObject = document.querySelector('div.content-all-user-object input.select-all-user');

  selectUserObject.addEventListener('change', (elem) => {
    elem.preventDefault();
    displayUserObject(users, selectUserObject.value);
    selectUserObject.value = `${selectUserObject.value}`;
  })

}

async function displayTabUser() {
  const options = {
    method: 'GET',

    headers: {
      'Content-Type': 'application/json',
      'Authorization': getToken()
    },
  };

  const response = await fetch('http://localhost:8080/users/getAll', options); // fetch return a promise => we wait for the response

  if (!response.ok) throw new Error(`fetch error : ${response.status} : ${response.statusText}`);

  const users = await response.json(); // json() returns a promise => we wait for the data$

  const contentAllUsers = document.querySelector('.content-all-user');
  contentAllUsers.innerHTML = displayListUser(users);

  const selectUser = document.querySelector('input.select-all-user');


  selectUser.addEventListener('change', (elem) => {
    elem.preventDefault();
    displayUserInfo(users, selectUser.value);
    selectUser.value = `${selectUser.value}`;

    const updateRoleButtons = document.querySelectorAll('button.update-role-button');
    updateRoleButtons.forEach((btn) => {
      btn.addEventListener('click', (e) => {
        e.preventDefault();
        const idUser = btn.dataset?.id;
        const role = btn.dataset?.role;
        const roleUser = document.querySelector('.section-user p.role-user');

        if(Number(role) === 0){
          roleUser.innerHTML = "Utilisateur";
        } else if (Number(role) === 1) {
          roleUser.innerHTML = "Aidant(e)";
        } else {
          roleUser.innerHTML = "Responsable";
        }

        changeRole(idUser, role);
      })
    })
  })

}

function displayListUser(users) {
  let allInfo = `
    <input type="text" list="users" class="form-control input select-all-user" style="max-width:350px; margin-right:10px">
    <button class="btn rounded-0 warning-color-btn">Chercher</button>
<datalist id="users">


    `;

  users?.forEach((user) => {
    allInfo += `
      <option value="${user.email}">${user.firstName} ${user.lastName} - ${user.email}</option>
    `;
  });

  allInfo += `
    </datalist>
    
  `;
  return allInfo;
}

function displayUserInfo(arrayUser, userEmail) {
  const infoUser = document.querySelector('div.info-user');
  infoUser.innerHTML = "";

  const userIndex = arrayUser.findIndex((element) => element.email === userEmail);
  const user = arrayUser[userIndex];

  let phoneUser;
  let buttonToHelper;

  if(user.phone === null){
    phoneUser = "non communiqué";
  } else {
    phoneUser = user.phone;
  }
  const userConnected = getAuthenticatedUser();
  if(user.id !== userConnected.id){
    if(user.role === 0){
      buttonToHelper = `<button type="submit" class="btn py-2 px-3 rounded-0 me-1 update-role-button btn-outline-dark" data-role="1" data-id="${user.id}">Définir comme aidant(e)</button><button type="submit" class="btn rounded-0 py-2 px-3 update-role-button btn-outline-dark" data-role="2" data-id="${user.id}">Définir comme responsable</button>`;
    } else if (user.role === 1){
      buttonToHelper = `<button type="submit" class="btn py-2 px-3 rounded-0 me-1 update-role-button btn-outline-dark" data-role="0" data-id="${user.id}">Définir comme utilisateur</button><button type="submit" class="btn rounded-0 py-2 px-3 update-role-button btn-outline-dark" data-role="2" data-id="${user.id}">Définir comme responsable</button>`;
    } else {
      buttonToHelper = `<button type="submit" class="btn py-2 px-3 rounded-0 me-1 update-role-button btn-outline-dark" data-role="0" data-id="${user.id}">Définir comme utilisateur</button><button type="submit" class="btn py-2 px-3 rounded-0 me-1 update-role-button btn-outline-dark" data-role="1" data-id="${user.id}">Définir comme aidant(e)</button>`;
    }
  } else {
    buttonToHelper = "<p>Il s'agit de vous.</p>";
  }

  if(user.image === null || user.image === undefined || !user.image) {
    user.image = "https://plchldr.co/i/500x350?bg=111111";
  }

  let roleUser;
  if(user.role === 0){
    roleUser = "Utilisateur";
  } else if(user.role === 1){
    roleUser = "Aidant(e)";
  } else {
    roleUser = "Responsable";
  }

  infoUser.innerHTML = `
    <div class="col-6 col-md-3">
      <img src="${user.image}" alt="plchldr.co">
    </div>
    <div class="col-6">
      <h2>${user.firstName} ${user.lastName}</h2>
      <p class="role-user">${roleUser}</p>
      <p>Email : ${user.email}</p>
      <p>Téléphone : ${phoneUser}</p>
      ${buttonToHelper}
      <p class="info-toHelper" data-id="${user.id}"></p>
    </div>
  `;

}

async function displayUserObject(arrayUser, userEmail) {
  const infoUser = document.querySelector('div.info-user-object');
  infoUser.innerHTML = "";

  const userIndex = arrayUser.findIndex(
      (element) => element.email === userEmail);

  const options = {
    method: 'GET',
    headers: {
      'Authorization': token
    },
  };

  const response = await fetch(
      `http://localhost:8080/object/getAllObjectsOfAUser/${arrayUser[userIndex].id}`, options); // fetch return a promise => we wait for the response

  if (!response.ok) throw new Error(
      `fetch error : ${response.status} : ${response.statusText}`);

  const objects = await response.json(); // json() returns a promise => we wait for the data$

  infoUser.innerHTML = displayObjects(objects);

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
    allInfo = "<h5 class='text-center'>Cette personne ne possède pas d'objet.</h5>";
  } else {
    allInfo = "";
    objects?.forEach((object) => {
      let classNameSellInfo;
      const dateDepose = new Date(object.datePassage);

      const datePropose = new Date(object.proposalDate);

      if(object.state === "AS" || object.state === "AC") {
        classNameSellInfo = "info_available";
      } else if(object.state === "PS") {
        classNameSellInfo = "info_available";
      } else if(object.state === "SO") {
        classNameSellInfo = "info_sell";
      } else if(object.state === "PR") {
        classNameSellInfo = "info_propose";
      }

      allInfo += `
      <div class="single-object link-object col-12 row" data-id="${object.id}" data-idType="${object.idType}" data-uri="/objets?IdObject=${object.id}">
        <div class="col-6 toClick-object" data-id="${object.id}">
          <img alt="plchldr.co" style="max-width: 450px!important;" class="image-object" data-id="${object.id}">
        </div>
        
        <div class="col-6">
          <div class="informations_object toClick-object" data-id="${object.id}">
              <span class="info_object_disponibility ${classNameSellInfo}">${object.stateComplete}</span>
              <h3>${object.description}</h3>
              <h6>${object.nameType}</h6>
              <p><b>Heure et date de dépôt : ${object.arrivalTime}h à ${object.departureTime}h le ${dateDepose.toLocaleDateString()}</b></p>
              <p><b>Date de proposition de l'objet : ${datePropose.toLocaleDateString()}</b></p>
              <p><b>Prix : ${object.sellingPrice} €</b></p>
          </div>
      </div>
      <hr class="mb-5 mt-5">`;
      getImageByObject(object.id);
    })
  }


  return allInfo;
}

async function changeRole(idUser, role) {
  const options = {
    method: 'POST',
    body: JSON.stringify({
      'newRole': role
    }),
    headers: {
      'Content-Type': 'application/json',
      'Authorization': token
    },
  };

  const response = await fetch(`http://localhost:8080/users/changeRoleUser/${idUser}`, options); // fetch return a promise => we wait for the response

  if (!response.ok) throw new Error(
      `fetch error : ${response.status} : ${response.statusText}`);

  if (!response.ok) throw new Error(`fetch error : ${response.status} : ${response.statusText}`);

  const infoUser = document.querySelector(`p.info-toHelper[data-id="${idUser}"]`);

  infoUser.innerHTML = "Rôle mis à jour";
  DashboardPage();

}

async function getImageByObject(id) {
  const response = await fetch(`http://localhost:8080/object/getImageObject/${id}`);

  if (!response.ok) throw new Error(
      `fetch error : ${response.status} : ${response.statusText}`);

  const image = await response.json();

  const imgBalise = document.querySelector(`img.image-object[data-id="${id}"]`);
  if(image.object_image === null || image.object_image === undefined || !image.object_image) {
    image.object_image = "https://plchldr.co/i/300x200?bg=111111";
  }
  imgBalise.setAttribute('src', image.object_image);
}


function clickSamedi(){
  const btnDate = document.querySelector(`button.input-date-btn`);
  const inputDate = document.querySelector(`input.input-date`);
  const infoDate = document.querySelector(`div.info-date`);
  btnDate.addEventListener('click', (elem) => {
    elem.preventDefault();
    const val = inputDate.value;
    const date = new Date(val);
    const dateInt = date.getUTCDay();
    infoDate.innerHTML = "";

    if(dateInt !== 6){
      infoDate.innerHTML = "<p style='color:red'>Vous devez selectionner un Samedi.</p>";
    } else {
      const valueDate = inputDate.value.split("-");
      const newDate = new Date(valueDate[0], valueDate[1]-1, valueDate[2]);
      sendDisponibility(newDate.getTime(), document.querySelector(`input.radio-time`).checked);
    }
  });

}

async function sendDisponibility(dateTimeStamp, matin) {

  const options = {
    method: 'POST',
    body: JSON.stringify({
        "day": dateTimeStamp,
        "isMorning": matin
      }),
    headers: {
      'Content-Type': 'application/json',
      'Authorization': token
    },
  };

  const response = await fetch(`http://localhost:8080/available/addAvailable`, options); // fetch return a promise => we wait for the response

  if (!response.ok) throw new Error(
      `fetch error : ${response.status} : ${response.statusText}`);

  await response.json();
  const infoDate = document.querySelector(`div.info-date`);
  infoDate.innerHTML = "<p>La date a bien été enregistrée.</p>";


}

export default DashboardPage;
