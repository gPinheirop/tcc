import axios from "axios";

async function makeApiCall(url) {
  const params = {
    text: "coffee",
  };
  let start = 0;
  let end = 0;
  try {
    start = Date.now();
    const response = await axios.get(url, { params });
    end = Date.now();
  } catch (error) {
    console.error(`Erro ao acessar ${url}:`, error.message);
  }
  return end - start;
}

const BASE_URL = "http://localhost:8080/search";

const POSTGRES_URL = "/postgres/fts/indexed-text";
const POSTGRES_URL_FREQUENCY = "/postgres/fts/indexed-text/frequency-rank";
const POSTGRES_URL3 = "/postgres/fts/indexed-text/context-dependent-rank";
const POSTGRES_URL_EXTRA = "/postgres/contains/text";

const ELASTIC_URL = "/es/fts/indexed-text";

async function executeRequests(ite) {
  let arr = [];
  for (let index = 0; index < ite; index++) {
    arr.push(await makeApiCall(BASE_URL + POSTGRES_URL_FREQUENCY));
  }
  return arr;
}

const results = await executeRequests(5);
console.log(results);

function getMinMaxAvg(arr) {
  if (arr.length === 0) {
    return { min: null, max: null, avg: null };
  }

  const min = Math.min(...arr);
  const max = Math.max(...arr);

  const sum = arr.reduce((acc, val) => acc + val, 0);
  const avg = sum / arr.length;

  return { min, max, avg };
}
const { min, max, avg } = getMinMaxAvg(results);

console.log("mínimo = ", min, " máximo = ", max, " média = ", avg);
