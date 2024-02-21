// eslint-disable-next-line import/named,import/no-extraneous-dependencies
import Chart from 'chart.js/auto';
import {
  getAuthenticatedUser,
  getToken,
  isAuthenticated
} from "../../utils/auths";
import Navigate from "../Router/Navigate";
import Navbar from "../Navbar/Navbar";

const StatisticsPage = () => {
  const main = document.querySelector('main .row');
  if (!isAuthenticated()) {
    Navigate('/');
  } else {
    const user = getAuthenticatedUser();
    main.innerHTML = `
<div style="height:75vh;" class="row">
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
        <div class="col">
        <div class="row ps-5">
        <div class="col-12">
<h3>Rechercher par période</h3>
<p>Toutes les statistiques fournies se basent sur la période à laquelle les objets ont été créés.</p>
</div>
<div class="col-12">
      <form id="get-statistics">
      <div class="input-group mb-3">
        <span class="input-group-text">Du</span>
        <input type="date" class="form-control" id="start-date" name="start-date">
        <span class="input-group-text">Au</span>
        <input type="date" class="form-control" id="end-date" name="end-date">
        <button id="submit-stats" type="submit" class="btn btn-success">Chercher</button>
      </div>
      </form> 
</div>
<div class="col-12">
<canvas id="myChart" width="auto" height="100"></canvas>
</div>
</div>
    </div>
        
</div>
</div>`;
  }
  Navbar();
  const now = new Date;
  // eslint-disable-next-line no-unused-expressions
  document.getElementById('end-date').value = `${now.getFullYear()  }-${  padTo2Digits(now.getMonth() + 1)  }-${  padTo2Digits(now.getDate())}`;
  now.setMonth(now.getMonth() - 3);
  document.getElementById('start-date').value = `${now.getFullYear()  }-${  padTo2Digits(now.getMonth() + 1)  }-${  padTo2Digits(now.getDate())}`;
  const formStats = document.getElementById("get-statistics");
  formStats.addEventListener('submit', getStatistics);
  document.getElementById("submit-stats").click();
};
let chart;
async function getStatistics(e) {
  e.preventDefault();
  const start = document.getElementById("start-date").value;
  const end = document.getElementById("end-date").value;

  const options = {
    method: 'POST',
    body: JSON.stringify({
      "start-date":start,
      "end-date":end
    }),

    headers: {
      'Content-Type': 'application/json',
      'Authorization': getToken()
    },
  };

  const response = await fetch(`http://localhost:8080/object/getStatistics`, options);

  if (!response.ok) throw new Error(
      `fetch error : ${response.status} : ${response.statusText}`);

  const object = await response.json();

  const ctx = document.getElementById("myChart").getContext('2d');

  const data = {
    type: 'bar',
        data: {
    labels: ['Objets acceptés', 'Objets déposés en magasin', 'Objets vendus', 'Objets retirés de la vente'],
        datasets: [{
      data: [object["accepted-objects"], object["dropped-objects"], object["sold-objects"], object["withdrawn-objects"]],
      backgroundColor: [
        'rgba(255, 99, 132, 0.2)',
        'rgba(54, 162, 235, 0.2)',
        'rgba(255, 206, 86, 0.2)',
        'rgba(75, 192, 192, 0.2)',
      ],
      borderColor: [
        'rgba(255, 99, 132, 1)',
        'rgba(54, 162, 235, 1)',
        'rgba(255, 206, 86, 1)',
        'rgba(75, 192, 192, 1)',
      ],
      borderWidth: 1
    }]
  },
    options: {
      plugins: {
        legend: {
          display: false
        }
      },
      scales: {
        y: {
          beginAtZero: true
        }
      }
    }
  }
  // eslint-disable-next-line no-new
  if (chart) {
    chart.destroy();
    chart = new Chart(ctx, data);
  } else {
    chart = new Chart(ctx, data);
  }
  chart.defaults.global.legend.display = false;
}

function padTo2Digits(num) {
  return num.toString().padStart(2, '0');
}

export default StatisticsPage;
