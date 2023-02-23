import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Player } from '../Player';

@Component({
  selector: 'app-winner-modal',
  template: `
    <h2 mat-dialog-title>{{ data.winner ? 'The winner is ' + data.winner.name : 'It was a tie no one won!' }}</h2>
    <div mat-dialog-content>
      <p>{{ data.logs }}</p>
    </div>
    <div mat-dialog-actions>
      <button mat-button (click)="restart()">Restart</button>
      <button mat-button (click)="close()">Change Pokemon</button>
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

  restart() {
    this.dialogRef.close('restart');
  }
}