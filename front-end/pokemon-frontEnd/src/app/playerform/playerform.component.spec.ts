import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlayerformComponent } from './playerform.component';

describe('PlayerformComponent', () => {
  let component: PlayerformComponent;
  let fixture: ComponentFixture<PlayerformComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PlayerformComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PlayerformComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
