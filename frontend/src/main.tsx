import { ConfigContext } from "@config/config-context";
import { loadConfig } from "@config/config-loader";
import { QueryClientProvider } from "@tanstack/react-query";
import { mockApiClient } from "@testutils/mock-api-client";
import { mockQueryClient } from "@testutils/mock-query-client";
import { apiClient } from "@utils/ApiClient";
import { queryClient } from "@utils/QueryClient";
import type { AxiosInstance } from "axios";
import { createContext, StrictMode, Suspense } from "react";
import { createRoot } from "react-dom/client";
import { RouterProvider } from "react-router-dom";
import { router } from "./router";

import "./main.css";

const config = await loadConfig();

const savedTheme = localStorage.getItem("theme");

if (savedTheme) {
  document.documentElement.setAttribute("data-theme", savedTheme);
}

const client =
  import.meta.env.VITE_USE_MOCK === "true" ? mockQueryClient : queryClient;
const api =
  import.meta.env.VITE_USE_MOCK === "true" ? mockApiClient : apiClient;

export const ApiClientContext = createContext<AxiosInstance | null>(null);

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <ConfigContext.Provider value={config}>
      <Suspense fallback={<></>}>
        <ApiClientContext.Provider value={api}>
          <QueryClientProvider client={client}>
            <RouterProvider router={router} />
          </QueryClientProvider>
        </ApiClientContext.Provider>
      </Suspense>
    </ConfigContext.Provider>
  </StrictMode>,
);
