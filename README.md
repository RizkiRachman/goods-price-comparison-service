<a id="readme-top"></a>

[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![MIT License][license-shield]][license-url]

<br />
<div align="center">
  <h3 align="center">Goods Price Comparison Service</h3>

  <p align="center">
    Extract, store, and compare product prices from receipt images — find the best deals across stores.
    <br />
    <a href="docs/ARCHITECTURE_HYBRID.md"><strong>Explore the docs »</strong></a>
    <br />
    <br />
    <a href="https://github.com/RizkiRachman/goods-price-comparison-service/issues/new?template=bug-report---.md">Report Bug</a>
    &middot;
    <a href="https://github.com/RizkiRachman/goods-price-comparison-service/issues/new?template=feature-request---.md">Request Feature</a>
  </p>
</div>

<details>
  <summary>Table of Contents</summary>
  <ol>
    <li><a href="#about-the-project">About The Project</a></li>
    <li><a href="#built-with">Built With</a></li>
    <li><a href="#getting-started">Getting Started</a></li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details>

## About The Project

Shoppers waste time and money guessing which store offers the best price for their groceries and daily necessities. This service solves that by letting users scan receipt images, automatically extract product prices using OCR, and query where to buy items cheapest across multiple stores.

Key capabilities:
- Upload receipt images and extract product names, prices, and quantities automatically
- Store price history in a searchable database
- Find the cheapest store for any item
- Optimize multi-item shopping routes
- Track price trends and receive drop alerts

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Built With

[![Spring Boot][Spring Boot]][Spring-url]
[![Java][Java]][Java-url]
[![PostgreSQL][PostgreSQL]][PostgreSQL-url]
[![Maven][Maven]][Maven-url]
[![Docker][Docker]][Docker-url]
[![Kubernetes][Kubernetes]][Kubernetes-url]
[![Google Cloud][Google Cloud]][GoogleCloud-url]
[![GitHub Actions][GitHub Actions]][GitHubActions-url]

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.9+
- PostgreSQL 14+ (production only)
- Docker (optional, local development)

### Installation

1. Clone the repository
   ```sh
   git clone https://github.com/RizkiRachman/goods-price-comparison-service.git
   cd goods-price-comparison-service
   ```
2. Install shared libraries
   ```sh
   git clone https://github.com/RizkiRachman/common-utils-java.git
   cd common-utils-java && mvn clean install && cd ..

   git clone https://github.com/RizkiRachman/common-exception-java.git
   cd common-exception-java && mvn clean install && cd ..
   ```
3. Run database migrations
   ```sh
   mvn flyway:migrate -Pflyway \
     -Ddatabase-name=goods-price-service \
     -Ddatabase-username=your_user \
     -Ddatabase-password=your_password
   ```
4. Start the application
   ```sh
   mvn spring-boot:run
   ```

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Usage

Upload a receipt image, and the system processes it asynchronously:

```sh
# Upload receipt
curl -X POST http://localhost:8080/api/v1/receipts \
  -F "image=@receipt.jpg"

# Poll status
curl http://localhost:8080/api/v1/receipts/{id}/status

# Find cheapest price
curl "http://localhost:8080/api/v1/prices/cheapest?product=Milk&limit=5"
```

For detailed API documentation, see the [Developer Guide](docs/DEVELOPER_GUIDE.md#getting-started).

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Roadmap

- [x] Receipt OCR with Google Vision / Gemini
- [x] Price database with historical tracking
- [x] Multi-store price comparison
- [ ] Shopping route optimization
- [ ] Price drop alerts and notifications
- [ ] Mobile app integration

See the [open issues](https://github.com/RizkiRachman/goods-price-comparison-service/issues) for a full list of proposed features.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Contributing

Contributions are what make the open source community an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the project
2. Create your branch (`ANEH-YYYYMMDD-SHORTDESC1-SHORTDESC2-SHORTDESC3`)
3. Commit your changes (`feat(scope): brief description`)
4. Push to the branch
5. Open a Pull Request

See [CONTRIBUTING.md](CONTRIBUTING.md) for full guidelines.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## License

Distributed under the MIT License. See `LICENSE` for more information.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Contact

Rizki Rachman - [@RizkiRachman](https://github.com/RizkiRachman)

Project Link: [https://github.com/RizkiRachman/goods-price-comparison-service](https://github.com/RizkiRachman/goods-price-comparison-service)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Acknowledgments

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Img Shields](https://shields.io) for repository badges
- [Best-README-Template](https://github.com/othneildrew/Best-README-Template) for the README structure

<p align="right">(<a href="#readme-top">back to top</a>)</p>

[contributors-shield]: https://img.shields.io/github/contributors/RizkiRachman/goods-price-comparison-service.svg?style=for-the-badge
[contributors-url]: https://github.com/RizkiRachman/goods-price-comparison-service/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/RizkiRachman/goods-price-comparison-service.svg?style=for-the-badge
[forks-url]: https://github.com/RizkiRachman/goods-price-comparison-service/network/members
[stars-shield]: https://img.shields.io/github/stars/RizkiRachman/goods-price-comparison-service.svg?style=for-the-badge
[stars-url]: https://github.com/RizkiRachman/goods-price-comparison-service/stargazers
[issues-shield]: https://img.shields.io/github/issues/RizkiRachman/goods-price-comparison-service.svg?style=for-the-badge
[issues-url]: https://github.com/RizkiRachman/goods-price-comparison-service/issues
[license-shield]: https://img.shields.io/github/license/RizkiRachman/goods-price-comparison-service.svg?style=for-the-badge
[license-url]: https://github.com/RizkiRachman/goods-price-comparison-service/blob/main/LICENSE
[Spring Boot]: https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white
[Spring-url]: https://spring.io/projects/spring-boot
[Java]: https://img.shields.io/badge/Java_17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white
[Java-url]: https://www.oracle.com/java/
[PostgreSQL]: https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white
[PostgreSQL-url]: https://www.postgresql.org/
[Maven]: https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white
[Maven-url]: https://maven.apache.org/
[Docker]: https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white
[Docker-url]: https://www.docker.com/
[Kubernetes]: https://img.shields.io/badge/Kubernetes-326CE5?style=for-the-badge&logo=kubernetes&logoColor=white
[Kubernetes-url]: https://kubernetes.io/
[Google Cloud]: https://img.shields.io/badge/Google_Cloud-4285F4?style=for-the-badge&logo=googlecloud&logoColor=white
[GoogleCloud-url]: https://cloud.google.com/
[GitHub Actions]: https://img.shields.io/badge/GitHub_Actions-2088FF?style=for-the-badge&logo=githubactions&logoColor=white
[GitHubActions-url]: https://github.com/features/actions
