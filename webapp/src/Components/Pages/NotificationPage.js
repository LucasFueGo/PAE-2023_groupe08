
import Navbar from "../Navbar/Navbar";
import gif from "../../img/loader2.gif";
import {getAuthenticatedUser, getToken} from "../../utils/auths";

const NotificationPage = () => {
  const isConnected = getAuthenticatedUser();
  if(isConnected) {
    const main = document.querySelector('main .row');
    main.innerHTML = `
  <div class="info_top">
    <h1 class="text-center mt-5 mb-5">Mes notifications</h1>
  </div>
  <div class="container rounded mt-5 mb-5">
    <div class="row">
      <div class="col-md-12 content-list-notification" style="display:flex;flex-wrap: wrap;justify-content: flex-start">
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
  const token = getToken();

  const response = await fetch(`http://localhost:8080/notification/getAllNotification?token=${token}`);

  if (!response.ok) throw new Error(
      `fetch error : ${response.status} : ${response.statusText}`);

  const notifs = await response.json();

  const main = document.querySelector('div.content-list-notification');
  main.innerHTML = displayNotifs(notifs);


  const btnNotifications = document.querySelectorAll('button.btn-notification');

  btnNotifications.forEach((btnNotification) => {
    btnNotification.addEventListener('click', (elem) => {
      elem.preventDefault();
      const notifId = btnNotification.dataset?.id;
      acceptNotification(notifId);
    })
  });
}


function displayNotifs(notifs) {
  let allInfo;
  if(notifs.length === 0){
    allInfo = "<h5 class='text-center'>Vous n'avez pas de notification.</h5>";
  } else {
    allInfo = "";
    notifs?.forEach((notif) => {
      const notifDate = new Date(notif.notificationDate);
      let btnNot;
      let text;

      if(!notif.read){
        btnNot = `<button class="btn btn-danger btn-notification" data-id="${notif.id}">Marquer comme lu</button>`;
        text = `<b>${notif.notificationMessage}</b>`;
      } else {
        btnNot = ``;
        text = `<p>${notif.notificationMessage}</p>`;
      }

      allInfo += `
      <div class="link-notif col-6 row" data-id="${notif.id}">
          <div class="informations_notification">
              <h6>${notifDate.toLocaleDateString()}</h6>
              ${text}
              <br>
              ${btnNot}
          </div>
      </div>`;
    })
  }

  return allInfo;
}

async function acceptNotification(id) {
  // const userInfo = getAuthenticatedUser();
  const token = getToken();
  const options = {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
  };

  const response = await fetch(
      `http://localhost:8080/notification/readNotification/${id}?token=${token}`, options);

  if (!response.ok) throw new Error(
      `fetch error : ${response.status} : ${response.statusText}`);

  const elementNotif = document.querySelector(`div.link-notif[data-id="${id}"] button`);
  const elementNotifText = document.querySelector(`div.link-notif[data-id="${id}"] b`);
  elementNotif.style.display = "none";
  elementNotifText.style.fontWeight = "normal";
}

export default NotificationPage;