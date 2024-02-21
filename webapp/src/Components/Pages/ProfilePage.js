// eslint-disable-next-line import/named
import {
  getAuthenticatedUser, getToken,
  isAuthenticated, updateAuthenticatedUser,
} from "../../utils/auths";
import Navigate from "../Router/Navigate";
import Navbar from "../Navbar/Navbar";

const ProfilPage = (text) => {
  const main = document.querySelector('main .row');
  if(!isAuthenticated()){
    Navigate('/');
  }else{
    const user = getAuthenticatedUser();
    main.innerHTML = `<div class="container"><div class="row justify-content-center align-items-center">
<div class="col-6">
      <input id="update-image" accept="image/png, image/jpeg"  type="file" class="form-control" style="display:none;">
    <div class=" image d-flex flex-column rounded-5 p-5 justify-content-center align-items-center">
    <div id="update-profile-img">
        <img id="current-profile-img" src="${user.image === null ? 'default.png' : user.image}" alt="User's profile picture">
          <div id="overlay" class="d-flex justify-content-center align-items-center">
             <i class="fa-solid fa-pen-to-square"></i>
          </div>
    </div>
            <form id="update-profile-form">
                <div class="form-group row mt-2">
                    <div class="col-md-6"><label class="labels">Nom</label><input id="update-lastname" type="text" class="form-control" value="${user.lastName}"></div>
                    <div class="col-md-6"><label class="labels">Prénom</label><input id="update-firstname" type="text" class="form-control" value="${user.firstName}"></div>
                </div>
                <div class="form-group row mt-3">
                    <div class="col-md-12"><label class="labels">Numéro de téléphone</label><input id="update-phone" type="text" class="form-control" value="${user.phone}"></div>
                    <div class="col-md-12"><label class="labels">Adresse email</label><input id="update-email" type="text" class="form-control" value="${user.email}" disabled></div>
                </div>
                <div class="form-group row mt-3">
                  <div class="col-md-12">
                    <label class="labels">Mot de passe</label><input id="update-pwd" type="password" class="form-control" placeholder="**********"></div>
                  </div>
                <div class="mt-5 text-center"><button class="btn btn-primary profile-button" type="submit">Mettre à jour</button></div>
            </form></div>
</div><div id="toast-container" class="toast-container" style="position:absolute;bottom:0;right:1rem;"></div>
</div>
`;
    const toastContainer = document.getElementById("toast-container");
    if(text){
      toastContainer.innerHTML =`<div class="toast align-items-center show text-white bg-info" role="alert" aria-live="assertive" aria-atomic="true" style="position: relative;bottom: 6rem;right: -0.5rem;">
  <div class="d-flex">
    <div class="toast-body">
      <i class="fa-regular fa-circle-check"></i> Le profil a été mis à jour.
    </div>
    <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
  </div>
</div>`;

    }
  }
  document.getElementById("update-profile-img").addEventListener('click', () => {
      document.getElementById("update-image").click();
  })

  const form = document.getElementById("update-profile-form");
  form.addEventListener('submit', onUpdate);

  // Wait until the file input is changed
  document.getElementById("update-image").onchange = function() {
    const reader = new FileReader();
    reader.onload = function() {
      const image = new Image();
      image.onload = function() {
        const canvas = document.createElement("canvas");
        const ctx = canvas.getContext("2d");

        canvas.width = 150;
        canvas.height = 150;
        ctx.drawImage(image, 0, 0, image.width,image.height,0,0,150,150);
        document.getElementById("current-profile-img").src = canvas.toDataURL("image/jpg");
      }
      image.src = reader.result;
    };
    reader.readAsDataURL(this.files[0]);
  };
};

async function onUpdate(event) {
  event.preventDefault();
  const lastname = document.querySelector("#update-lastname").value;
  const firstname = document.querySelector("#update-firstname").value;
  const phone = document.querySelector("#update-phone").value;
  const email = document.querySelector("#update-email").value;
  const password = document.querySelector("#update-pwd").value;

  const image = document.querySelector("#update-image");

  const userEntier = new FormData();
  userEntier.append('email', email);
  userEntier.append('password', password);
  userEntier.append('firstname', firstname);
  userEntier.append('lastname', lastname);
  userEntier.append('phone', phone);
  userEntier.append('file', image.files[0]);

  const options = {
    method: 'POST',
    body: userEntier,
    headers:{
      'Authorization': getToken()
    }
  };

  const response = await fetch('http://localhost:8080/users/update', options);
  if (!response.ok) {
    throw new Error(`fetch error : ${response.status} : ${response.statusText}`)
  };
  const authenticatedUser = await response.json();
  updateAuthenticatedUser(authenticatedUser);

  Navbar();
  ProfilPage(true);
}

export default ProfilPage;
