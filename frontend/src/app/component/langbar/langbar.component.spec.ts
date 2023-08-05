import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LangbarComponent } from './langbar.component';

describe('LangbarComponent', () => {
  let component: LangbarComponent;
  let fixture: ComponentFixture<LangbarComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LangbarComponent]
    });
    fixture = TestBed.createComponent(LangbarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
