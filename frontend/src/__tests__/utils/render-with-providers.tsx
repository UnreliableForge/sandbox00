import { ConfigContext } from "@config/config-context";
import { QueryClientProvider } from "@tanstack/react-query";
import { render } from "@testing-library/react";
import { mockApiClient } from "@testutils/mock-api-client";
import type { ReactNode } from "react";
import {
  createMemoryRouter,
  RouterProvider,
  type RouteObject,
} from "react-router-dom";
import { ApiClientContext } from "../../main";

export function renderWithProviders(
  ui: ReactNode,
  options?: { config?: any; queryClient?: any },
) {
  const config = options?.config ?? {};
  const queryClient = options?.queryClient;
  return render(
    <ConfigContext.Provider value={config}>
      <ApiClientContext.Provider value={mockApiClient}>
        <QueryClientProvider client={queryClient}>{ui}</QueryClientProvider>
      </ApiClientContext.Provider>
    </ConfigContext.Provider>,
  );
}

export function renderWithRouter(_ui: ReactNode, routes: RouteObject[]) {
  const router = createMemoryRouter(routes);
  return renderWithProviders(<RouterProvider router={router} />);
}
