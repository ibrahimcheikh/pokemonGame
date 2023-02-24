import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Player } from '../Player';

@Component({
  selector: 'app-winner-modal',
  template: `
    <h2 mat-dialog-title>{{  'The winner is ' + data.winner?.name}}</h2>
    <div mat-dialog-content>
      <p>{{ data.logs }}</p>
    </div>
    <div mat-dialog-actions>
      <button mat-button (click)="restart()">Restart</button>
      <button mat-button (click)="changePokemon()">Change Pokemon</button>
    </div>
  `,
})
export class WinnerModalComponent {
  constructor(
    public dialogRef: MatDialogRef<WinnerModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { winner: Player | null; logs: string }
  ) {}

  close() {
    this.dialogRef.close();
  }

  changePokemon() {
    this.dialogRef.close("change-pokemon");
  }

  restart() {
    this.dialogRef.close('restart');
  }
}