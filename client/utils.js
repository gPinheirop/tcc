import { Worker } from "worker_threads";

export function getMinMaxAvg(arr) {
  if (arr.length === 0) {
    return { min: null, max: null, avg: null };
  }

  const min = Math.min(...arr);
  const max = Math.max(...arr);

  const sum = arr.reduce((acc, val) => acc + val, 0);
  const avg = sum / arr.length;

  return { min, max, avg };
}

export function calculateMode(array) {
  const frequency = {};

  array.forEach((value) => {
    frequency[value] = (frequency[value] || 0) + 1;
  });

  const highestFrequency = Math.max(...Object.values(frequency));

  const modes = Object.keys(frequency)
    .filter((valor) => frequency[valor] === highestFrequency)
    .map(Number);

  return {
    values: modes,
    frequency: highestFrequency,
  };
}

export function calculateMedian(values) {
  const sortedValues = [...values].sort((a, b) => a - b);

  const middle = Math.floor(sortedValues.length / 2);

  if (sortedValues.length % 2 !== 0) {
    return sortedValues[middle];
  } else {
    return { median1: sortedValues[middle - 1], median2: sortedValues[middle] };
  }
}

export function calculateStandardDeviation(values) {
  const average = values.reduce((acc, val) => acc + val, 0) / values.length;

  const deviation =
    values.reduce((acc, val) => acc + Math.pow(val - average, 2), 0) /
    values.length;

  return { standardDeviation: Math.sqrt(deviation) };
}

export function createWorker(url, params) {
  return new Promise((resolve, reject) => {
    const worker = new Worker("./worker.js", { workerData: { url, params } });

    worker.on("message", resolve);
    worker.on("error", reject);
    worker.on("exit", (code) => {
      if (code !== 0) reject(new Error(`Worker finish: ${code}`));
    });
  });
}
