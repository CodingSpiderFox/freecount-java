import React from 'react';
import MenuItem from 'app/shared/layout/menus/menu-item';
import { Translate, translate } from 'react-jhipster';
import { NavDropdown } from './menu-components';

export const EntitiesMenu = props => (
  <NavDropdown
    icon="th-list"
    name={translate('global.menu.entities.main')}
    id="entity-menu"
    data-cy="entity"
    style={{ maxHeight: '80vh', overflow: 'auto' }}
  >
    <>{/* to avoid warnings when empty */}</>
    <MenuItem icon="asterisk" to="/project">
      <Translate contentKey="global.menu.entities.project" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/project-settings">
      <Translate contentKey="global.menu.entities.projectSettings" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/bill">
      <Translate contentKey="global.menu.entities.bill" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/bill-position">
      <Translate contentKey="global.menu.entities.billPosition" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/project-member">
      <Translate contentKey="global.menu.entities.projectMember" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/product">
      <Translate contentKey="global.menu.entities.product" />
    </MenuItem>
    {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
  </NavDropdown>
);
