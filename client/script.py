import time
import requests
import concurrent.futures

BASE_URL = "http://localhost:8080/search"
POSTGRES_URL = '/postgres/fts/indexed-text'
POSTGRES_URL_FREQUENCY = '/postgres/fts/indexed-text/frequency-rank'
POSTGRES_URL3 = '/postgres/fts/indexed-text/context-dependent-rank'
POSTGRES_URL_EXTRA = '/postgres/contains/text'

ELASTIC_URL = '/es/fts/indexed-text'

def test_endpoint(url):
    start_time = int(time.time() * 1000)
    params = {
        'text': 'coffee'
    }
    requests.get(url, params = params) #caso seja preciso usar a reponse
    end_time = int(time.time() * 1000)
    x = end_time - start_time
    print(f'{x} ms ')
    return x

def teste_paralel_endpoint():
    # Use ThreadPoolExecutor to send requests in parallel
    with concurrent.futures.ThreadPoolExecutor() as executor:
        # Submit the function make_request num_requests times
        futures = [executor.submit(test_endpoint, url) for _ in range(200)]

        # Wait for all futures to complete and handle the results
        for future in concurrent.futures.as_completed(futures):
            result = future.result()
            # Handle the result if needed, like printing the response text
            if result:
                print(f"Received: {result}")

def sequencial_calls(iterations, url):
        for _ in range(iterations):
            test_endpoint(url)

def main():
    # teste_potgres = test_endpoint(BASE_URL+ POSTGRES_URL)
    # teste_elsatic = test_endpoint(BASE_URL+ ELASTIC_URL)
    # print('postgres = ', teste_potgres)
    # print('elastic = ', teste_elsatic)
    # print(test_endpoint(BASE_URL+ POSTGRES_URL_FREQUENCY))
    # print(test_endpoint(BASE_URL+ ELASTIC_URL))
    # sequencial_calls(100, BASE_URL + ELASTIC_URL)
    sequencial_calls(5, BASE_URL + ELASTIC_URL)

    # teste_paralel_endpoint(BASE_URL + POSTGRES_URL)

main()


# TODO Add teste em paralelo
# TODO Testar jmeter
