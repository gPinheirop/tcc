import { workerData, parentPort } from 'worker_threads';
import axios from 'axios';

const { url, params } = workerData;

async function makeRequest(url, params) {
  const start = Date.now();
  try {
    await axios.get(url, { params });
    const duration = Date.now() - start;
    return duration;
  } catch (error) {
    const duration = Date.now() - start;
    throw duration;
  }
}

makeRequest(url, params)
  .then((duration) => parentPort.postMessage(duration))
  .catch((duration) => parentPort.postMessage(duration));
