import { loadingBarReducer as loadingBar } from 'react-redux-loading-bar';

import locale from './locale';
import authentication from './authentication';
import applicationProfile from './application-profile';

import administration from 'app/modules/administration/administration.reducer';
import userManagement from './user-management';
// prettier-ignore
import project from 'app/entities/project/project.reducer';
// prettier-ignore
import projectSettings from 'app/entities/project-settings/project-settings.reducer';
// prettier-ignore
import bill from 'app/entities/bill/bill.reducer';
// prettier-ignore
import billPosition from 'app/entities/bill-position/bill-position.reducer';
// prettier-ignore
import projectMember from 'app/entities/project-member/project-member.reducer';
// prettier-ignore
import product from 'app/entities/product/product.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const rootReducer = {
  authentication,
  locale,
  applicationProfile,
  administration,
  userManagement,
  project,
  projectSettings,
  bill,
  billPosition,
  projectMember,
  product,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
  loadingBar,
};

export default rootReducer;
