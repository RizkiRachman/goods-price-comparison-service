# Project Roadmap

This document outlines the development phases and milestones for the Goods Price Comparison Service.

---

## Phase 1: MVP (Minimum Viable Product)

**Timeline**: Weeks 1-4

**Goal**: Core functionality working - receipt upload, OCR processing, and basic price storage

### Tasks

#### Week 1: Project Setup & Database ✅
- [x] Initialize Spring Boot project with Maven
- [x] Setup PostgreSQL with Flyway migrations
- [ ] Create database schema (stores, products, price_records)
- [x] Configure development environment (local profile with H2)
- [x] Setup CI/CD pipeline with GitHub Actions
- [x] Implement code quality checks (JaCoCo ready)

#### Week 2: Core Entities & Repositories 🔄
- [ ] Create entity classes (Store, Product, PriceRecord)
- [ ] Implement JPA repositories
- [ ] Add database indexes for performance
- [ ] Write unit tests for repositories (100% coverage)
- [x] Setup Testcontainers for integration tests

#### Week 3: Receipt Upload & OCR
- [ ] Implement file upload endpoint (`POST /api/receipts/upload`)
- [ ] Integrate Google Vision API for OCR
- [ ] Create receipt parser to extract products/prices
- [ ] Store processed data in database
- [ ] Add Tesseract as fallback OCR
- [ ] Write comprehensive tests for OCR service

#### Week 4: Price Search API
- [ ] Implement price search endpoint (`POST /api/prices/search`)
- [ ] Add filtering by date range, location, store
- [ ] Create price comparison logic
- [ ] Implement pagination for large result sets
- [ ] Add caching with Redis (optional)
- [ ] Write API documentation

### Deliverables (Phase 1)
- 🔄 Working backend with database (PostgreSQL configured, H2 for local dev)
- ⬜ Receipt upload and OCR processing
- ⬜ Basic price search functionality
- ⬜ Unit tests with 90%+ coverage (pending entity implementation)

**Note**: Coverage check temporarily disabled for initial setup phase
- ✅ API documentation

---

## Phase 2: Core Features

**Timeline**: Weeks 5-8

**Goal**: Shopping optimization, price history, and user features

### Tasks

#### Week 5: Shopping Optimization Algorithm
- [ ] Design optimization algorithm
- [ ] Implement store selection logic (max items per store)
- [ ] Calculate cheapest prices for remaining items
- [ ] Generate optimized shopping route
- [ ] Add estimated travel time between stores
- [ ] Create optimization endpoint (`POST /api/shopping/optimize`)
- [ ] Write algorithm tests

#### Week 6: Price History & Trends
- [ ] Implement price history tracking
- [ ] Create trend analysis (`GET /api/products/trend/{id}`)
- [ ] Add price charts (weekly, monthly views)
- [ ] Detect price patterns (increasing, decreasing, stable)
- [ ] Store historical data with partitioning
- [ ] Optimize queries for time-series data

#### Week 7: User Authentication & Profiles
- [ ] Implement JWT authentication
- [ ] Create user registration/login endpoints
- [ ] Add user profiles and preferences
- [ ] Implement saved shopping lists
- [ ] Add favorite stores feature
- [ ] Setup email service for notifications

#### Week 8: Price Alerts
- [ ] Create alert subscription system (`POST /api/alerts/subscribe`)
- [ ] Implement price monitoring job (scheduled task)
- [ ] Add email notifications for price drops
- [ ] Create alert management endpoints
- [ ] Add push notifications (Firebase)
- [ ] Write alert processing tests

### Deliverables
- ✅ Shopping route optimization
- ✅ Price history and trends
- ✅ User authentication system
- ✅ Price alerts functionality
- ✅ Enhanced API with user features

---

## Phase 3: Advanced Features

**Timeline**: Weeks 9-12

**Goal**: Machine learning, mobile app, and social features

### Tasks

#### Week 9: Machine Learning Integration
- [ ] Research price prediction models
- [ ] Collect training data from price history
- [ ] Implement time-series forecasting
- [ ] Add price prediction endpoint
- [ ] Create recommendation engine ("You might also like")
- [ ] Evaluate model accuracy
- [ ] Deploy ML model

#### Week 10: Store Locator & GPS
- [ ] Integrate Google Maps API
- [ ] Add store coordinates to database
- [ ] Implement store locator by current location
- [ ] Calculate distances and travel times
- [ ] Add route optimization with real traffic
- [ ] Create map view in web UI
- [ ] Add "nearby deals" feature

#### Week 11: Mobile App (React Native)
- [ ] Setup React Native project
- [ ] Implement receipt camera capture
- [ ] Create mobile UI for price search
- [ ] Add offline mode (cached data)
- [ ] Implement push notifications
- [ ] Test on iOS and Android
- [ ] Deploy to app stores

#### Week 12: Social Features
- [ ] Add deal sharing functionality
- [ ] Create community deal feed
- [ ] Implement deal voting (hot/not)
- [ ] Add deal comments/discussions
- [ ] Create store rating system
- [ ] Add "verified deals" badge
- [ ] Implement abuse reporting

### Deliverables
- ✅ ML-powered price predictions
- ✅ Store locator with GPS
- ✅ Mobile app (iOS & Android)
- ✅ Social features and community
- ✅ Complete end-to-end solution

---

## Phase 4: Scale & Optimize

**Timeline**: Weeks 13-16+

**Goal**: Production readiness, performance, and scaling

### Tasks

#### Performance Optimization
- [ ] Database query optimization
- [ ] Implement read replicas for queries
- [ ] Add Redis caching layer
- [ ] Optimize OCR processing (async queues)
- [ ] CDN for static assets
- [ ] Implement database sharding (if needed)

#### Production Readiness
- [ ] Security audit and penetration testing
- [ ] Setup monitoring (Prometheus, Grafana)
- [ ] Add centralized logging (ELK stack)
- [ ] Create backup and disaster recovery plan
- [ ] Implement rate limiting and throttling
- [ ] Setup SSL certificates
- [ ] Create production deployment guide

#### Scaling
- [ ] Horizontal scaling with Kubernetes
- [ ] Implement message queues (RabbitMQ/Kafka)
- [ ] Add load balancing
- [ ] Optimize database partitioning strategy
- [ ] Implement multi-region deployment
- [ ] Setup auto-scaling policies

### Deliverables
- ✅ Production-ready system
- ✅ Monitoring and alerting
- ✅ High availability and scalability
- ✅ Enterprise-grade security

---

## Future Enhancements (Post-Phase 4)

### Ideas for Future Versions

**Feature Enhancements:**
- [ ] Support for multiple countries/currencies
- [ ] Barcode scanning for quick product lookup
- [ ] Voice search for products
- [ ] Integration with grocery delivery services
- [ ] Recipe-based shopping list generator
- [ ] Budget tracking and analytics
- [ ] Family/team account sharing

**Technical Improvements:**
- [ ] GraphQL API alongside REST
- [ ] WebSocket support for real-time updates
- [ ] Blockchain for price verification
- [ ] AI-powered receipt categorization
- [ ] Smart shopping assistant (chatbot)

**Business Features:**
- [ ] Store analytics dashboard for retailers
- [ ] Promotional campaign management
- [ ] Affiliate marketing integration
- [ ] White-label solution for supermarkets
- [ ] API for third-party integrations

---

## Progress Tracking

### Current Status

- **Phase**: Phase 1 - Week 1
- **Completed**: 25% (Week 1: Project Setup ✅)
- **Next Milestone**: Phase 1 - Week 2 (Core Entities & Repositories)

### Completed ✅

- [x] Initialize Spring Boot project with Maven
- [x] Setup PostgreSQL with Flyway migrations (configured)
- [x] Configure development environment (local profile with H2)
- [x] Setup CI/CD pipeline structure
- [x] Implement code quality checks (JaCoCo, Checkstyle ready)

### In Progress 🔄

- [ ] Create database schema (stores, products, price_records tables)
- [ ] Create entity classes (Store, Product, PriceRecord)
- [ ] Implement JPA repositories

### Legend

- ⬜ Not started
- 🔄 In progress
- ✅ Completed
- ⏸️ Blocked

---

## How to Contribute

1. Pick a task from the current phase
2. Create a feature branch: `git checkout -b feature/task-name`
3. Implement with tests (100% coverage for new code)
4. Update this roadmap when task is complete
5. Create PR following our [AI Guidelines](../.ai/AGENTS.md)

---

## Timeline Visualization

```
Month 1          Month 2          Month 3          Month 4
Week: 1  2  3  4  5  6  7  8  9  10 11 12 13 14 15 16
      ├─ Phase 1: MVP ─┤├─ Phase 2: Core ─┤├─ Phase 3 ─┤├─ Phase 4 ─┤
      │                ││                 ││           ││           │
      └────────────────┘└─────────────────┘└───────────┘└───────────┘
      Database         Shopping          ML & Mobile   Production
      OCR              Price History     Social        Scale
```

---

**Last Updated**: [AUTO-UPDATED BY CI]

**Next Review Date**: Weekly on Mondays