---
name: front-end-expert
description: Front-end engineering — React, state management, performance, accessibility, styling, and testing
license: MIT
compatibility: opencode
metadata:
  role: developer
  domain: frontend
---

# Front-End Expertise

## Component Architecture

### Structure
- One component per file. File name matches component name.
- Presentational vs container: presentational receives data and renders, container manages state and side effects
- Keep components small — if a component has >200 lines or >5 props, consider splitting
- Default export for the component, named exports for types and helpers

### Props
- Use TypeScript interfaces for all props, not inline types
- Prefix callback props with `on` — `onClick`, `onSubmit`, `onChange`
- Destructure props in the function signature, not inside the body
- Avoid spreading props to child elements — be explicit
- Default values at destructure, not in useEffect
- `children` should be `React.ReactNode`, not a custom prop

### State Management

| State type | Tool | Where |
|-----------|------|-------|
| Local UI state | `useState`, `useReducer` | Inside component |
| Shared server state | React Query / SWR / RTK Query | Custom hooks |
| Global client state | Context, Zustand, Redux | Store files |
| URL state | `useSearchParams`, `useParams` | Router hooks |
| Form state | React Hook Form / Formik | Form component |

Rules:
- State should live as close to where it's used as possible
- If two sibling components share state, lift it to the parent — don't use global state as a shortcut
- `useReducer` when state has multiple related fields or complex transitions
- Avoid putting derived data in state — compute it from source data

### Effects
- Every `useEffect` has a cleanup when it subscribes, polls, or sets intervals
- When `useEffect` depends on props/state, include them in the dependency array (or use a ref)
- If you're using `useEffect` to transform data, you probably shouldn't — compute during render
- Fetching in `useEffect`? Use a data-fetching library instead (React Query, SWR)

---

## TypeScript Patterns

```typescript
// Prefer interfaces for public API shapes
interface UserProps {
  id: string;
  name: string;
  email: Email;
}

// Use type for unions, intersections, and complex types
type ApiState<T> =
  | { status: 'idle' }
  | { status: 'loading' }
  | { status: 'success'; data: T }
  | { status: 'error'; error: Error };

// Branded types for domain primitives
type Email = string & { __brand: 'email' };

// Never use `any`. Prefer `unknown` and narrow.
// Never use `as` casts unless you've validated the type.
```

---

## Performance

### Render Optimization
- `React.memo` for heavy components that render often with the same props
- `useMemo` for expensive computations, `useCallback` for stable callback references
- Keys: stable, unique, and not array indices for dynamic lists
- Code-split route-level components with `React.lazy` and `Suspense`
- Virtualize long lists (react-window, react-virtuoso)

### Bundle Size
- Tree-shakeable imports — import from specific paths, not barrels
- Dynamic imports for large libraries not needed on initial render
- Image optimization: lazy loading (`loading="lazy"`), responsive sizes, WebP/AVIF
- Monitor bundle size in CI, set budgets

### Loading & UX
- Show loading states immediately — don't wait for data to arrive
- Skeleton screens over spinners for content-heavy pages
- Optimistic updates for user actions that usually succeed
- Handle all states: loading, empty, error, success

---

## Accessibility (a11y)

- All interactive elements must be keyboard accessible
- Buttons use `<button>`, not `<div onClick>`
- Images have `alt` text (empty `alt=""` for decorative images)
- Forms: labels associated with inputs (`htmlFor`/`id`)
- Color is never the only indicator of state
- Headings: hierarchical (`h1` → `h2` → `h3`), no skipping levels
- Live regions for dynamic content changes (`aria-live="polite"`)
- Test with screen reader (VoiceOver, NVDA) before shipping

---

## Styling

| Approach | When to use |
|----------|-------------|
| CSS Modules | Default — scoped, no runtime cost |
| Tailwind | Utility-first, rapid prototyping, design system |
| CSS-in-JS (styled-components) | Dynamic styles based on props |
| CSS custom properties | Theming, design tokens |

Rules:
- Avoid inline styles except for truly dynamic values
- Design tokens for colors, spacing, typography — not raw values
- Mobile-first media queries
- CSS specificity kept flat — avoid deep nesting (Sass) or `!important`
- Responsive: test at 320px, 768px, 1024px, 1440px

---

## Testing

| Layer | Tool | What to test |
|-------|------|-------------|
| Unit | Vitest / Jest + Testing Library | Pure functions, hooks, utilities |
| Component | Testing Library | Render, user interaction, accessibility |
| Integration | MSW + Testing Library | Page flows, data fetching, error states |
| E2E | Playwright / Cypress | Critical user journeys |

Guidelines:
- Test behavior, not implementation — `getByRole('button')` not `getByTestId('submit-btn')`
- User-centric queries: `getByRole` > `getByLabelText` > `getByText` > `getByTestId`
- `screen.getBy...` over destructured `container.queryBy...`
- Mock network at the boundary (MSW), not inside components
- Snapshot tests for small, stable components only — they break too easily

---

## Common Pitfalls

| Mistake | Why it hurts | Fix |
|---------|-------------|-----|
| Prop drilling | Components coupled, hard to refactor | Context, composition, or colocation |
| Inline arrow functions in render | Breaks `React.memo`, causes re-renders | `useCallback` or extract |
| Too many re-renders | Slow UI, janky interactions | Profile with React DevTools, memo where needed |
| Mutating state directly | React won't re-render | Immutable updates (setState, immer) |
| Giant useEffect | Hard to reason about, runs too often | Split into separate effects |
| No error boundaries | Entire app crashes on one component error | Wrap sections in ErrorBoundary |
| Over-engineering | Abstraction for a single use case | YAGNI — build what's needed now |

---

## Token Optimization

When working with frontend code and component libraries, optimize context usage:

```bash
/skill token-optimize
```

**Key practices:**
- Focus on relevant components and hooks
- Search for specific patterns before reading entire files
- Summarize component architecture concisely
- Reuse context when iterating on UI components
