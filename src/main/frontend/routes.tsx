import {RouterConfigurationBuilder} from '@vaadin/hilla-file-router/runtime.js';
import Flow from 'Frontend/generated/flow/Flow';
import SurveyAnswer from "Frontend/views/a/{token}";

export const {router, routes} = new RouterConfigurationBuilder()
  .withReactRoutes([
    {
      path: '/a/:token',
      element: <SurveyAnswer/>
    }
  ])
  .withFallback(Flow)
  .protect()
  .build();
