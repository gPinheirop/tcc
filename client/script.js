import axios from "axios";

import {
  words,
  BASE_URL,
  POSTGRES_URL,
  POSTGRES_URL_FREQUENCY,
  POSTGRES_URL_CONTEXT_DEPENDENT_RANK,
  POSTGRES_URL_EXTRA,
  ELASTIC_URL,
} from "./consts.js";

import {
  calculateMedian,
  calculateMode,
  calculateStandardDeviation,
  createWorker,
  getMinMaxAvg,
} from "./utils.js";

async function getAPICallTimeDelta(url) {
  const ramdomIndex = Math.floor(Math.random() * 10);
  const params = {
    text: words[ramdomIndex],
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

async function executeRequests(url, iterations) {
  let arr = [];
  for (let index = 0; index < iterations; index++) {
    arr.push(await getAPICallTimeDelta(url));
  }
  return arr;
}

async function sendMultipleRequests(url, requestsNumber) {
  const endpoint = url;
  const numberOfRequests = requestsNumber;

  const workers = Array.from({ length: numberOfRequests }, () => {
    const ramdomIndex = Math.floor(Math.random() * 10);
    const params = {
      text: words[ramdomIndex],
    };
    return createWorker(endpoint, params);
  });

  try {
    const results = await Promise.all(workers);
    return results;
  } catch (error) {
    console.error("Erro ao enviar requisições:", error);
  }
}

// const results = await executeRequests(BASE_URL + POSTGRES_URL, 100);
// const results = await executeRequests(BASE_URL + POSTGRES_URL_FREQUENCY, 100);
// const results = await executeRequests(BASE_URL + POSTGRES_URL_CONTEXT_DEPENDENT_RANK, 100);
// const results = await executeRequests(BASE_URL + ELASTIC_URL, 100);
// const results = await executeRequests(BASE_URL + POSTGRES_URL_EXTRA, 100);

// const results = await sendMultipleRequests(BASE_URL + POSTGRES_URL, 100);
// const results = await sendMultipleRequests(
//   BASE_URL + POSTGRES_URL_FREQUENCY,
//   100
// );
// const results = await sendMultipleRequests(
//   BASE_URL + POSTGRES_URL_CONTEXT_DEPENDENT_RANK,
//   100
// );
// const results = await sendMultipleRequests(BASE_URL + ELASTIC_URL);
const results = await sendMultipleRequests(BASE_URL + POSTGRES_URL_EXTRA, 100);

const { min, max, avg } = getMinMaxAvg(results);
const { median1, median2 } = calculateMedian(results);
const { values, frequency } = calculateMode(results);
const { standardDeviation } = calculateStandardDeviation(results);

console.log("mínimo = ", min, " máximo = ", max, " média = ", avg);
console.log("Mediana => mediana 1 = ", median1, " mediana 2 = ", median2);
console.log("Moda => valores = ", values, " frequência = ", frequency);
console.log("Desvio padrão = ", standardDeviation);
